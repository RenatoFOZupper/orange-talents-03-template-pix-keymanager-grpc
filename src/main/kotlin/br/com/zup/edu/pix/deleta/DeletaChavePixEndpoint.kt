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

        //1. Recebe um RequestGrpc e converte para o DeletaChavePix
        val deletaChavePix = request.toDelete()

        //3. Chama a classe DeletaChavePixService que contém toda lógica de validação e exclusão no banco
        service.deleta(deletaChavePix)

        //3. Retorna um objeto de reposta com a confirmação de exclusão
        responseObserver.onNext(DeletaChavePixResponse.newBuilder()
                                                    .setMessagem("Chave ${request.pixId} excluída com sucesso!")
                                                    .build()
        )
        responseObserver.onCompleted()

    }

}


