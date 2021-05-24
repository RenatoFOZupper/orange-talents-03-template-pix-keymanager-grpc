package br.com.zup.edu.chavepix

import br.com.zup.edu.ChavePixServiceGrpc
import br.com.zup.edu.NovaChavePixRequest
import br.com.zup.edu.NovaChavePixResponse
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional

@Singleton
open class ChavePixServer(@Inject val chavePixRepository: ChavePixRepository) : ChavePixServiceGrpc.ChavePixServiceImplBase() {

    val logger = LoggerFactory.getLogger(ChavePixServer::class.java)

    @Transactional
    open override fun cadastrar(request: NovaChavePixRequest, responseObserver: StreamObserver<NovaChavePixResponse>) {

        logger.info("Recebendo os dados de entrada do request: $request")



        val novaChavePix = ChavePix(
            idErpItau = request.idErpItau,
            tipoChave = request.tipoChave,
            valorChave = request.valorChave,
            tipoConta = request.tipoConta
        )

        val chavePix = chavePixRepository.save(novaChavePix)

        val response = NovaChavePixResponse.newBuilder()
                                            .setPixId(chavePix.id.toString())
                                            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()

    }
}


