package br.com.zup.edu.pix.registra

import br.com.zup.edu.integrations.bacen.*
import br.com.zup.edu.integrations.bacen.requests.CreatePixKeyRequest
import br.com.zup.edu.integrations.bacen.responses.BankAccountResponse
import br.com.zup.edu.integrations.bacen.responses.OwnerResponse
import br.com.zup.edu.shared.exceptions.ChavePixExistenteException
import br.com.zup.edu.integrations.itau.ContasDeClientesNoItauClient
import br.com.zup.edu.pix.*
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.lang.RuntimeException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid


@Validated
@Singleton
class NovaChaveService(@Inject val repository: ChavePixRepository,
                       @Inject val itauClient: ContasDeClientesNoItauClient,
                       @Inject val bcbClient: ContasDeClientesNoBacenClient
) {


    @Transactional
    fun registra(@Valid novaChave: NovaChavePix): ChavePix {

        val LOGGER = LoggerFactory.getLogger(this::class.java)

        //1.Valida se a chave informada existe no sistema
        if (repository.existsByChave(novaChave.chave)) {
            throw ChavePixExistenteException("Chave Pix '${novaChave.chave}' existente")
        }

        //2.Busca dados da conta no ERP do Itau
        val response = itauClient.buscaContaPorTipo(novaChave.clienteId!!, novaChave.tipoConta!!.name)
        val conta = response.body()?.toModel() ?: throw IllegalStateException("Cliente não encontrado no Itau")

        //3.Grava no db
        val chave = novaChave.toModel(conta)
        repository.save(chave)
        LOGGER.info("Salvou a chave pix no itau: $chave")

        //4. Converte os dados do cliente Itau para o request do Bacen Client
        val requestBacen = CreatePixKeyRequest.toModelBacen(chave)

        //5. Salva no Bacen
        val responseBacen = bcbClient.registraNovaChavePix(requestBacen)
        val chavePixBacen = responseBacen.body() ?: throw IllegalStateException("Erro ao registrar chave pix no Banco Central do Brasil")
        LOGGER.info("Resposta do BacenClient: $responseBacen")

        //6. Caso o tipo de chave seja aleatoria, atualiza a chave no sistema do itau
        chave.update(chavePixBacen.key)
        LOGGER.info("Atualizou no itau a chave aleatoria que foi gerada no bacen: $chave")

        return chave

    }

}

