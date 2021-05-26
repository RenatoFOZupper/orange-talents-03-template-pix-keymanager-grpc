package br.com.zup.edu.pix

import br.com.zup.edu.KeymanagerGrpcServiceGrpc
import br.com.zup.edu.RegistraChavePixRequest
import br.com.zup.edu.RegistraChavePixResponse
import br.com.zup.edu.shared.annotations.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class ChavePixEndpoint(@Inject val novaChaveService: NovaChaveService) : KeymanagerGrpcServiceGrpc.KeymanagerGrpcServiceImplBase() {

    override fun registra(
        request: RegistraChavePixRequest,
        responseObserver: StreamObserver<RegistraChavePixResponse>
    ) {

        val novaChave = request.toModel()
        val chaveCriada = novaChaveService.registra(novaChave)

        responseObserver.onNext(RegistraChavePixResponse.newBuilder()
                                    .setClientId(chaveCriada.clienteId.toString())
                                    .setPixId(chaveCriada.id.toString())
                                    .build())

        responseObserver.onCompleted()


    }

}


