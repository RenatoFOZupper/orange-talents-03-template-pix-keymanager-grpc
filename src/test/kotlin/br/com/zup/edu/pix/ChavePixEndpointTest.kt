package br.com.zup.edu.pix

import br.com.zup.edu.KeymanagerGrpcServiceGrpc
import br.com.zup.edu.RegistraChavePixRequest
import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta
import io.grpc.Channel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class ChavePixEndpointTest(
    @Inject val grpcClient: KeymanagerGrpcServiceGrpc.KeymanagerGrpcServiceBlockingStub) {

    /*
    * Cenarios de testes:
    *
    * 1. Happy path - ok
    * 2. Não deve cadastrar chave existente
    * 3. Nova chave com dados inválidos
    * 4. Não consegue encontrar os dados do cliente Itau
    * 4.
    *
    * Obs: O grpc ñ retorna nulos, caso ele ñ consiga recuperar, ele vai retorna o valor default
    */

    @Inject
    lateinit var repository: ChavePixRepository

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }



    @Test
    fun `deve registrar uma nova chave`() {
        //cenario
        repository.deleteAll()

        // acao
        val request = RegistraChavePixRequest.newBuilder()
                                                .setClientId("c56dfef4-7901-44fb-84e2-a2cefb157890")
                                                .setTipoChave(TipoChave.CPF)
                                                .setChave("02467781054")
                                                .setTipoConta(TipoConta.CONTA_CORRENTE)
                                                .build()

        val response = grpcClient.registra(request)

        // validacao
        with(response) {
            assertNotEquals("", clientId)
            assertNotEquals("", pixId, "pixId: $pixId")
            assertTrue(repository.findAll().size <= 1)
        }

    }


    @Test
    fun `nao deve registrar chave existente`() {
        //cenario

        val chaveCadastrada = RegistraChavePixRequest.newBuilder()
                                                    .setClientId("c56dfef4-7901-44fb-84e2-a2cefb157890")
                                                    .setTipoChave(TipoChave.CPF)
                                                    .setChave("02467781054")
                                                    .setTipoConta(TipoConta.CONTA_CORRENTE)
                                                    .build()
        grpcClient.registra(chaveCadastrada)

        //acao
        val novaRequest = RegistraChavePixRequest.newBuilder()
            .setClientId("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoChave(TipoChave.CPF)
            .setChave("02467781054")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()

        val error = assertThrows(StatusRuntimeException::class.java) {
            grpcClient.registra(novaRequest)
        }


        //validacao

        assertEquals(Status.ALREADY_EXISTS.code, error.status.code)
        assertEquals("Chave Pix '02467781054' existente", error.status.description)

    }

    @Factory
    class ClientsFactory {

        @Singleton
        fun stub(@GrpcChannel(GrpcServerChannel.NAME)channel: Channel) : KeymanagerGrpcServiceGrpc.KeymanagerGrpcServiceBlockingStub? {
            return KeymanagerGrpcServiceGrpc.newBlockingStub(channel)
        }

    }

}

