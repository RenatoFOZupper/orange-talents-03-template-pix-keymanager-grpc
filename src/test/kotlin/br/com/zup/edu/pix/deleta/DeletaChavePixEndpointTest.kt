package br.com.zup.edu.pix.deleta

import br.com.zup.edu.DeletaChavePixRequest
import br.com.zup.edu.KeymanagerRemoveGrpcServiceGrpc
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
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class DeletaChavePixEndpointTest(@Inject val repository: ChavePixRepository,
                                          @Inject val grpcClient: KeymanagerRemoveGrpcServiceGrpc.KeymanagerRemoveGrpcServiceBlockingStub) {


    lateinit var CHAVE_PIX: ChavePix


    @BeforeEach
    fun setupUp() {
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
        println("Salvo um registro de chave pix para cenario de teste")
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }


    /*
    * 1. Happy path - ok
    * 2. nao deve deletar se nao for o cliente dono da chave - ok
    * 3. dados invalidos - ok
    *
    */


    @Test
    fun `deve excluir uma chave com sucesso`() {
        //cenario

        val pixId = UUID.fromString(CHAVE_PIX.id.toString())
        val clienteId = UUID.fromString(CHAVE_PIX.clienteId.toString())


        //acao
        val response = grpcClient.deleta(DeletaChavePixRequest.newBuilder()
            .setPixId(pixId.toString())
            .setClientId(clienteId.toString())
            .build())


        //validacao
        with(response) {
            assertFalse(repository.existsById(pixId))
            assertTrue(repository.findAll().size < 1)
            assertEquals("Chave ${pixId.toString()} excluída com sucesso!", messagem)
            assertEquals(CHAVE_PIX.id, pixId)
            assertEquals(CHAVE_PIX.clienteId, clienteId)
        }

    }

    @Test
    fun `nao deve deletar se nao for o cliente dono da chave`() {

        //cenario

        val pixId = UUID.fromString(CHAVE_PIX.id.toString())
        val clienteId = "2ac09233-21b2-4276-84fb-d83dbd9f8bab"


        //acao
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.deleta(DeletaChavePixRequest.newBuilder()
                .setPixId(pixId.toString())
                .setClientId(clienteId)
                .build())
        }


        //validacao
        with(response) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals(Status.NOT_FOUND.cause, status.cause)
        }

    }

    @Test
    fun `nao deve deletar com dados invalidos`() {
        //cenario
        val pixId = ""
        val clienteId = ""

        //acao
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.deleta(DeletaChavePixRequest.newBuilder()
                .setPixId(pixId)
                .setClientId(clienteId)
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




    @MockBean(ContasDeClientesNoItauClient::class)
    fun itauErpClient(): ContasDeClientesNoItauClient {
        return Mockito.mock(ContasDeClientesNoItauClient::class.java)
    }

    @Factory
    class ClientsFactory {

        @Singleton
        fun stub(@GrpcChannel(GrpcServerChannel.NAME) channel: Channel): KeymanagerRemoveGrpcServiceGrpc.KeymanagerRemoveGrpcServiceBlockingStub{
            return KeymanagerRemoveGrpcServiceGrpc.newBlockingStub(channel)
        }

    }
}