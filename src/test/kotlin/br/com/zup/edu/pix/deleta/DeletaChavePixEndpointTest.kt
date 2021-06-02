package br.com.zup.edu.pix.deleta

import br.com.zup.edu.DeletaChavePixRequest
import br.com.zup.edu.KeymanagerRemoveGrpcServiceGrpc
import br.com.zup.edu.integrations.bacen.ContasDeClientesNoBacenClient
import br.com.zup.edu.integrations.bacen.requests.DeletePixKeyRequest
import br.com.zup.edu.integrations.bacen.responses.DeletePixKeyResponse
import br.com.zup.edu.integrations.itau.ContasDeClientesNoItauClient
import br.com.zup.edu.integrations.itau.responses.DadosDaContaResponse
import br.com.zup.edu.integrations.itau.responses.InstituicaoResponse
import br.com.zup.edu.integrations.itau.responses.TitularResponse
import br.com.zup.edu.pix.*
import io.grpc.Channel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class DeletaChavePixEndpointTest(@Inject val repository: ChavePixRepository,
                                          @Inject val grpcClient: KeymanagerRemoveGrpcServiceGrpc.KeymanagerRemoveGrpcServiceBlockingStub) {

    /*
  * 1. deve excluir uma chave pix com sucesso - Happy path - ok
  * 2. nao deve deletar chave pix quando retornar erro do BacenClient - ok
  * 3.
  * 4. nao deve deletar chave pix quando nao consta registro da chave - ok
  * 5. nao deve deletar chave pix com dados invalidos - ok
  */


    @Inject
    lateinit var bacenClient: ContasDeClientesNoBacenClient

    lateinit var CHAVE_PIX: ChavePix


    @BeforeEach
    fun setup() {
        CHAVE_PIX = repository.save(ChavePix(
            clienteId = UUID.fromString("5260263c-a3c1-4727-ae32-3bdb2538841b"),
            tipoChave = TipoChave.CPF,
            chave = "86135457004",
            tipoConta = TipoConta.CONTA_POUPANCA,
            conta = ContaAssociada(
                instituicao = "ITAÚ UNIBANCO S.A.",
                agencia = "0001",
                numeroDaConta = "291900",
                nomeDoTitular = "Yuri Matheus",
                cpfDoTitular = "86135457004"
            )
        )
        )
        println("Salva um registro de chave pix para cenario de teste")
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }


    @Test
    fun `deve excluir uma chave pix com sucesso`() {

        //cenario
        val pixId = UUID.fromString(CHAVE_PIX.id.toString())
        val clienteId = CHAVE_PIX.clienteId.toString()

        `when`(bacenClient.deletaChavePix(CHAVE_PIX.chave, DeletePixKeyRequest(key = CHAVE_PIX.chave,
            participant = "60701190"))).thenReturn(HttpResponse.ok())

        //acao
        val response = grpcClient.deleta(DeletaChavePixRequest.newBuilder()
                                                                .setPixId(pixId.toString())
                                                                .setClientId(clienteId)
                                                                .build())


        //validacao
        assertEquals("Chave $pixId excluída com sucesso!" ,response.messagem)
        assertTrue(repository.count() == 0L)
    }

    @Test
    fun `nao deve deletar chave pix quando retornar erro do BacenClient`() {

        //cenario

        `when`(bacenClient.deletaChavePix(CHAVE_PIX.chave, DeletePixKeyRequest(key = CHAVE_PIX.chave,
            participant = "60701190"))).thenReturn(HttpResponse.unprocessableEntity())

        //acao
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.deleta(DeletaChavePixRequest.newBuilder()
                .setPixId(CHAVE_PIX.id.toString())
                .setClientId(CHAVE_PIX.clienteId.toString())
                .build())
        }


        //validacao
        with(response) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals(Status.FAILED_PRECONDITION.cause, status.cause)
            assertEquals("Erro ao deletar chave Pix no Banco Central do Brasil", status.description)
        }
    }

    @Test
    fun `nao deve deletar chave pix quando chave pertence a outro cliente`() {

        //cenario
        val outroClienteId = UUID.randomUUID().toString()


        //acao
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.deleta(DeletaChavePixRequest.newBuilder()
                                                    .setPixId(CHAVE_PIX.chave)
                                                    .setClientId("5260263c-a3c1-4727-ae32-3bdb2538841b")
                                                    .build())
        }

        //validacao
        with(response) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }

    }


    @Test
    fun `nao deve deletar chave pix quando nao consta registro da chave`() {

        //cenario
        val pixIdInexistente = UUID.randomUUID().toString()


        //acao
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.deleta(DeletaChavePixRequest.newBuilder()
                .setPixId(pixIdInexistente)
                .setClientId(CHAVE_PIX.clienteId.toString())
                .build())
        }

        //validacao
        with(response) {
            assertEquals("Chave pix não encontrada ou não pertence ao cliente", status.description)
            assertEquals(Status.NOT_FOUND.code, status.code)
        }

    }


    @Test
    fun `nao deve deletar chave pix com dados invalidos`() {
        //cenario
        val pixId = ""
        val clienteId = ""

        //acao
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.deleta(DeletaChavePixRequest.newBuilder()
                .build())
        }


        //validacao
        with(response) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }








    private fun dadosDaContaResponse(): DadosDaContaResponse? {
        return DadosDaContaResponse(
            instituicao = InstituicaoResponse(nome = "ITAÚ UNIBANCO S.A.", ispb = "60701190"),
            agencia = "0001",
            numero = "291900",
            titular = TitularResponse(nome = "Rafael M C Ponte", cpf = "02467781054"),
            tipo = "CONTA_CORRENTE"
        )
    }

    private fun deleteChavePixBacenResponse(): DeletePixKeyResponse {
        return DeletePixKeyResponse(
            key = CHAVE_PIX.chave,
            participant = CHAVE_PIX.conta.instituicao,
            deletedAt = LocalDateTime.now().toString()
        )
    }




    @MockBean(ContasDeClientesNoItauClient::class)
    fun itauErpClient(): ContasDeClientesNoItauClient {
        return mock(ContasDeClientesNoItauClient::class.java)
    }

    @MockBean(ContasDeClientesNoBacenClient::class)
    fun bacenClient(): ContasDeClientesNoBacenClient {
        return mock(ContasDeClientesNoBacenClient::class.java)
    }

    @Factory
    class ClientsFactory {

        @Singleton
        fun stub(@GrpcChannel(GrpcServerChannel.NAME) channel: Channel): KeymanagerRemoveGrpcServiceGrpc.KeymanagerRemoveGrpcServiceBlockingStub? {
            return KeymanagerRemoveGrpcServiceGrpc.newBlockingStub(channel)
        }

    }
}