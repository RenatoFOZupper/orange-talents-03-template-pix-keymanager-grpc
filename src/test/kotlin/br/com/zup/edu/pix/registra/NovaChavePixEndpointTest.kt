package br.com.zup.edu.pix.registra

import br.com.zup.edu.KeymanagerRegisterGrpcServiceGrpc
import br.com.zup.edu.RegistraChavePixRequest
import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta
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
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class NovaChavePixEndpointTest(
    @Inject val grpcClient: KeymanagerRegisterGrpcServiceGrpc.KeymanagerRegisterGrpcServiceBlockingStub) {

    /*
    * Cenarios de testes:
    *
    * 1. Happy path - ok
    * 2. Não deve cadastrar chave existente
    * 3. Nova chave com dados inválidos
    * 4. Não consegue encontrar os dados do cliente Itau
    *
    *
    * Obs: O grpc ñ retorna nulos, caso ele ñ consiga recuperar, ele vai retorna o valor default
    */

    @Inject
    lateinit var repository: ChavePixRepository

    @Inject
    lateinit var itauErpClient: ContasDeClientesNoItauClient

    companion object {
        val CLIENTE_ID = UUID.randomUUID()
    }


    @BeforeEach
    fun cleanUp() {
        repository.deleteAll()
    }


    @Test
    fun `deve registrar uma nova chave pix`() {

        //cenario
        `when`(
            itauErpClient.buscaContaPorTipo(CLIENTE_ID.toString(), "CONTA_CORRENTE")
        ).thenReturn(HttpResponse.ok(dadosDaContaResponse()))

        //acao
        val response = grpcClient.registra(
            RegistraChavePixRequest.newBuilder()
                .setClientId(CLIENTE_ID.toString())
                .setTipoChave(TipoChave.CPF)
                .setChave("02467781054")
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build())

        //validacao
        with(response) {
            assertEquals(CLIENTE_ID.toString(), clientId)
            assertNotNull(pixId)
        }
    }

    @Test
    fun `deve retornar usuario invalido quando nao encontrar dados do usuario no ItauErp`() {
        //cenario
            `when`(itauErpClient.buscaContaPorTipo(CLIENTE_ID.toString(), "CONTA_CORRENTE"))
                .thenReturn(HttpResponse.notFound())

        //acao
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.registra(
                RegistraChavePixRequest.newBuilder()
                    .setClientId(CLIENTE_ID.toString())
                    .setTipoChave(TipoChave.CPF)
                    .setChave("02467781054")
                    .setTipoConta(TipoConta.CONTA_CORRENTE)
                    .build())
        }

        //validacao
        with(response) {
           assertEquals(Status.FAILED_PRECONDITION.code, status.code)
        }


    }


    @Test
    fun `deve retornar uma AlreadyExistsException para uma chave ja cadastrada`() {

        //cenario
        repository.save(
            ChavePix(
            clienteId = CLIENTE_ID,
            tipoChave = br.com.zup.edu.pix.TipoChave.CPF,
            chave = "02467781054",
            tipoConta = br.com.zup.edu.pix.TipoConta.CONTA_CORRENTE,
            conta = ContaAssociada(
                instituicao = "ITAÚ UNIBANCO S.A.",
                agencia = "0001",
                numeroDaConta = "291900",
                nomeDoTitular = "Rafael M C Ponte",
                cpfDoTitular = "02467781054"
            )
        )
        )

        //acao
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.registra(RegistraChavePixRequest.newBuilder()
                .setClientId(CLIENTE_ID.toString())
                .setTipoChave(TipoChave.CPF)
                .setChave("02467781054")
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build())
        }

        //validacao
        with(response) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Chave Pix '02467781054' existente", status.description)
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
        return mock(ContasDeClientesNoItauClient::class.java)
    }


    @Factory
    class ClientsFactory {

        @Singleton
        fun stub(@GrpcChannel(GrpcServerChannel.NAME)channel: Channel) : KeymanagerRegisterGrpcServiceGrpc.KeymanagerRegisterGrpcServiceBlockingStub? {
            return KeymanagerRegisterGrpcServiceGrpc.newBlockingStub(channel)
        }

    }

}

