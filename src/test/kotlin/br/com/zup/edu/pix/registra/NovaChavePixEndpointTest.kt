package br.com.zup.edu.pix.registra

import br.com.zup.edu.KeymanagerRegisterGrpcServiceGrpc
import br.com.zup.edu.RegistraChavePixRequest
import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta
import br.com.zup.edu.integrations.bacen.ContasDeClientesNoBacenClient
import br.com.zup.edu.integrations.bacen.TipoChaveBacen
import br.com.zup.edu.integrations.bacen.TipoContaBacen
import br.com.zup.edu.integrations.bacen.TipoPessoa
import br.com.zup.edu.integrations.bacen.requests.CreatePixKeyRequest
import br.com.zup.edu.integrations.bacen.responses.BankAccountResponse
import br.com.zup.edu.integrations.bacen.responses.CreatePixKeyResponse
import br.com.zup.edu.integrations.bacen.responses.OwnerResponse
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
import java.time.LocalDateTime
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
    * 2. não deve registrar chave quando não encontrar dados do cliente no itauErp
    * 3. não deve registrar duplicidade de chave
    * 4. não deve registrar chave no banco quando retornar erro do bacenClient
    *
    *
    * Obs: O grpc ñ retorna nulos, caso ele ñ consiga recuperar, ele vai retorna o valor default
    */

    @Inject
    lateinit var repository: ChavePixRepository

    @Inject
    lateinit var itauErpClient: ContasDeClientesNoItauClient

    @Inject
    lateinit var bacenClient: ContasDeClientesNoBacenClient

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


        `when`(
            bacenClient.registraNovaChavePix(createPixRequest())
        ).thenReturn(HttpResponse.ok(createPixKeyResponse()))

        //acao
        val response = grpcClient.registra(
            RegistraChavePixRequest.newBuilder()
                .setClientId(CLIENTE_ID.toString())
                .setTipoChave(TipoChave.CPF)
                .setChave("06628726061")
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build())

        //validacao
        with(response) {
            assertEquals(CLIENTE_ID.toString(), clientId)
            assertNotNull(pixId)
        }
    }


    @Test
    fun `nao deve registrar chave quando nao encontrar dados da conta do usuario no itauErp`() {
        //cenario
        `when`(itauErpClient.buscaContaPorTipo(CLIENTE_ID.toString(), "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.notFound())


        //acao
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.registra(
                RegistraChavePixRequest.newBuilder()
                    .setClientId(CLIENTE_ID.toString())
                    .setTipoChave(TipoChave.CPF)
                    .setChave("06628726061")
                    .setTipoConta(TipoConta.CONTA_CORRENTE)
                    .build())
        }

        //validacao
        with(response) {
            assertEquals("Cliente não encontrado no Itau", status.description)
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
        }
    }


    @Test
    fun `nao deve registrar chave Pix quando a mesma ja existe`() {

        //cenario
        val chaveSalva = repository.save(
            ChavePix(
            clienteId = CLIENTE_ID,
            tipoChave = br.com.zup.edu.pix.TipoChave.CPF,
            chave = "06628726061",
            tipoConta = br.com.zup.edu.pix.TipoConta.CONTA_CORRENTE,
            conta = ContaAssociada(
                instituicao = "ITAÚ UNIBANCO S.A.",
                agencia = "0001",
                numeroDaConta = "212233",
                nomeDoTitular = "Alberto Tavares",
                cpfDoTitular = "06628726061"
            )
        ))

        //acao
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.registra(RegistraChavePixRequest.newBuilder()
                .setClientId(CLIENTE_ID.toString())
                .setTipoChave(TipoChave.CPF)
                .setChave("06628726061")
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build())
        }

        //validacao
        with(response) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Chave Pix '${chaveSalva.chave}' existente", status.description)
        }
    }


    @Test
    fun `nao deve registrar chave no banco quando retornar erro do client do bacen`() {

        //cenario
        `when`(
            itauErpClient.buscaContaPorTipo(CLIENTE_ID.toString(), "CONTA_CORRENTE")
        ).thenReturn(HttpResponse.ok(dadosDaContaResponse()))


        `when`(
            bacenClient.registraNovaChavePix(createPixRequest())
        ).thenReturn(HttpResponse.unprocessableEntity())

        //acao
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.registra(RegistraChavePixRequest.newBuilder()
                .setClientId(CLIENTE_ID.toString())
                .setTipoChave(TipoChave.CPF)
                .setChave("06628726061")
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build())
        }

        //validacao
        with(response) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Erro ao registrar chave pix no Banco Central do Brasil", status.description)
            assertTrue(repository.findAll().size < 1)
        }
    }


    @Test
    fun `nao deve registrar com parametros invalidos`() {

        //cenario


        //acao
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.registra(RegistraChavePixRequest.newBuilder()
                .setClientId(CLIENTE_ID.toString())
                .build())
        }

        //validacao
        with(response) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("invalid parameters", status.description)
        }

    }



    private fun dadosDaContaResponse(): DadosDaContaResponse? {
        return DadosDaContaResponse(
            instituicao = InstituicaoResponse(nome = "ITAÚ UNIBANCO S.A.", ispb = "60701190"),
            agencia = "0001",
            numero = "212233",
            titular = TitularResponse(nome = "Alberto Tavares", cpf = "06628726061"),
            tipo = "CONTA_CORRENTE"
        )
    }

    private fun createPixRequest(): CreatePixKeyRequest {
        return CreatePixKeyRequest(
            keyType = TipoChaveBacen.CPF,
            key = "06628726061",
            bankAccount = bankAccount(),
            owner = owner()
        )
    }

    private fun bankAccount(): BankAccountResponse {
        return BankAccountResponse(
            participant = "60701190",
            branch = "0001",
            accountNumber = "212233",
            accountType = TipoContaBacen.CACC
        )
    }

    private fun owner(): OwnerResponse {
        return OwnerResponse(
            type = TipoPessoa.NATURAL_PERSON,
            name = "Alberto Tavares",
            taxIdNumber = "06628726061"
        )
    }

    private fun createPixKeyResponse(): CreatePixKeyResponse {
        return CreatePixKeyResponse(
            keyType = TipoChaveBacen.CPF,
            key = "06628726061",
            bankAccount = bankAccount(),
            owner = owner(),
            createdAt = LocalDateTime.now().toString()
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
        fun stub(@GrpcChannel(GrpcServerChannel.NAME)channel: Channel) : KeymanagerRegisterGrpcServiceGrpc.KeymanagerRegisterGrpcServiceBlockingStub? {
            return KeymanagerRegisterGrpcServiceGrpc.newBlockingStub(channel)
        }

    }

}

