package br.com.zup.edu.pix

import br.com.zup.edu.shared.exceptions.ChavePixExistenteException
import br.com.zup.edu.integrations.ContasDeClientesNoItauClient
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid


@Validated
@Singleton
class NovaChaveService(@Inject val repository: ChavePixRepository,
                       @Inject val itauClient: ContasDeClientesNoItauClient) {


    @Transactional
    fun registra(@Valid novaChave: NovaChavePix): ChavePix {

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

        return chave

    }

}
