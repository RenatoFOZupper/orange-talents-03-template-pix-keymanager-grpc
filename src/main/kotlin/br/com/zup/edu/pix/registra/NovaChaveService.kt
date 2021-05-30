package br.com.zup.edu.pix.registra

import br.com.zup.edu.integrations.bacen.*
import br.com.zup.edu.shared.exceptions.ChavePixExistenteException
import br.com.zup.edu.integrations.itau.ContasDeClientesNoItauClient
import br.com.zup.edu.pix.ChavePix
import br.com.zup.edu.pix.ChavePixRepository
import br.com.zup.edu.pix.TipoConta
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
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
        if (repository.existsByChave(novaChave.chave) ||
            repository.existsByClienteId(UUID.fromString(novaChave.clienteId))) {
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
        val bcbRequest = CreatePixKeyRequest(
            keyType = chave.tipoChave,
            key = chave.chave,
            bankAccount = BankAccountResponse(
                participant = "60701190",
                branch = conta.agencia,
                accountNumber = conta.numeroDaConta,
                accountType = chave.tipoConta.converteTipoContaBacen()
            ),
            owner = OwnerResponse(
                type = TipoPessoa.NATURAL_PERSON,
                name = conta.nomeDoTitular,
                taxIdNumber = conta.cpfDoTitular
            )
        )

        LOGGER.info("Bacen request: $bcbRequest")

        //5. Salva no Bacen
        bcbClient.registraNovaChavePix(bcbRequest)

        return chave

    }

}
