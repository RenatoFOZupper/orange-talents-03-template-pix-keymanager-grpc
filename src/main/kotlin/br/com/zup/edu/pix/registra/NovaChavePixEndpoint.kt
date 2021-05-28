package br.com.zup.edu.pix.registra

import br.com.zup.edu.KeymanagerRegisterGrpcServiceGrpc
import br.com.zup.edu.RegistraChavePixRequest
import br.com.zup.edu.RegistraChavePixResponse
import br.com.zup.edu.pix.toModel
import br.com.zup.edu.shared.annotations.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class NovaChavePixEndpoint(@Inject val novaChaveService: NovaChaveService) : KeymanagerRegisterGrpcServiceGrpc.KeymanagerRegisterGrpcServiceImplBase() {

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


