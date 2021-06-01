package br.com.zup.edu.pix.deleta

import br.com.zup.edu.integrations.bacen.ContasDeClientesNoBacenClient
import br.com.zup.edu.integrations.bacen.requests.DeletePixKeyRequest

import br.com.zup.edu.integrations.itau.ContasDeClientesNoItauClient
import br.com.zup.edu.pix.ChavePixRepository
import br.com.zup.edu.shared.exceptions.ChavePixNaoIdentificadaException
import io.grpc.Status


import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpStatus
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
        val chavePixItau = repository.findByIdAndClienteId(requestChavePixId, requestClienteId)
            .orElseThrow { ChavePixNaoIdentificadaException("Chave pix não encontrada ou não pertence ao cliente")}

        //3. Faz a exclusão da chave no sistema Itau
        repository.delete(chavePixItau)

        //4. Remove a chave do banco central
        val requestBacen = DeletePixKeyRequest(chavePixItau.chave)
        val response = bacenClient.deletaChavePix(key = requestBacen.key, request = requestBacen)

        //5. Retorna um erro caso não seja possivel deletar a chave do banco central do Brasil
        if (response.status != HttpStatus.OK) {
            throw IllegalStateException("Erro ao deletar chave Pix no Banco Central do Brasil")
        }
        
    }

}