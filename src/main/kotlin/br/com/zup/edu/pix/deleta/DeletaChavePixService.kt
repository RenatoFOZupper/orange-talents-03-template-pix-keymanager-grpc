package br.com.zup.edu.pix.deleta

import br.com.zup.edu.integrations.bacen.ContasDeClientesNoBacenClient
import br.com.zup.edu.integrations.bacen.DeletePixKeyRequest
import br.com.zup.edu.integrations.itau.ContasDeClientesNoItauClient
import br.com.zup.edu.pix.ChavePixRepository
import io.grpc.Status


import io.grpc.StatusRuntimeException
import org.slf4j.LoggerFactory

import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional


@Singleton
open class DeletaChavePixService(@Inject val repository: ChavePixRepository,
                                 @Inject val grcpClient: ContasDeClientesNoItauClient,
                                 @Inject val bacenClient: ContasDeClientesNoBacenClient) {

    @Transactional
    open fun deleta(deletaChavePix: DeletaChavePix) {

        val LOGGER = LoggerFactory.getLogger(DeletaChavePixService::class.java)


        //1.Recebe as chave PixId e ClienteId do request
        val requestClienteId = UUID.fromString(deletaChavePix.clienteId)
        val requestChavePixId = deletaChavePix.pixId

        //2. Verifica se existe a chave no banco
        val chaveDoCliente = repository.findByIdAndClienteId(requestChavePixId, requestClienteId)

        //3. Retorna uma exception amigavel caso nao conste a chave no banco
        if (chaveDoCliente.isEmpty) {
            throw StatusRuntimeException(Status.NOT_FOUND.withDescription("Não consta nenhum registro da " +
                    "chave $requestChavePixId no sistema, por favor verifique os dados e tente novamente."))
        }
        val chavePix = chaveDoCliente.get()

        //4. Em caso de sucesso, exclui a chave informada do sistema
        val chaveBacen = bacenClient.buscaPorChavePix(chavePix.chave)
        LOGGER.info("RequestBacen: $chaveBacen")

        bacenClient.deletaChavePix(chaveBacen.key, DeletePixKeyRequest(chaveBacen.key, chaveBacen.bankAccount.participant))


        repository.delete(chaveDoCliente.get())

    }

}