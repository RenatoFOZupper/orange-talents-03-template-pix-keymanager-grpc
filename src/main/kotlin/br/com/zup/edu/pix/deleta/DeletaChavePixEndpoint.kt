package br.com.zup.edu.pix.deleta

import br.com.zup.edu.DeletaChavePixRequest
import br.com.zup.edu.DeletaChavePixResponse
import br.com.zup.edu.KeymanagerRemoveGrpcServiceGrpc
import br.com.zup.edu.pix.toDelete
import br.com.zup.edu.shared.annotations.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class DeletaChavePixEndpoint(@Inject val service: DeletaChavePixService):
    KeymanagerRemoveGrpcServiceGrpc.KeymanagerRemoveGrpcServiceImplBase() {

    override fun deleta(request: DeletaChavePixRequest, responseObserver: StreamObserver<DeletaChavePixResponse>) {

        val deletaChavePix = request.toDelete()


        service.deleta(deletaChavePix)

        responseObserver.onNext(DeletaChavePixResponse.newBuilder()
                                                    .setMessagem("Chave ${request.pixId} excluída com sucesso!")
                                                    .build()
        )
        responseObserver.onCompleted()

    }

}


