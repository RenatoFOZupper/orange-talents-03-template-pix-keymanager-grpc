package br.com.zup.edu.integrations.bacen

import br.com.zup.edu.pix.TipoChave
import br.com.zup.edu.pix.TipoConta
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType.APPLICATION_XML


import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import javax.persistence.EnumType
import javax.persistence.Enumerated


@Consumes(APPLICATION_XML)
@Produces(APPLICATION_XML)
@Client("\${bacen.contas.url}")
interface ContasDeClientesNoBacenClient {

    @Get("/api/v1/pix/keys/{key}")
    fun buscaPorChavePix(@PathVariable key: String): PixKeyDetailsResponse

    @Post("/api/v1/pix/keys")
    fun registraNovaChavePix(@Body request: CreatePixKeyRequest): CreatePixKeyResponse

}

@Introspected
data class CreatePixKeyRequest(
    @Enumerated(EnumType.STRING)
    val keyType: TipoChave,
    val key: String,
    val bankAccount: BankAccountResponse,
    val owner: OwnerResponse
)

@Introspected
data class CreatePixKeyResponse(
    @Enumerated(EnumType.STRING)
    val keyType: TipoChave,
    val key: String,
    val bankAccount: BankAccountResponse,
    val owner: OwnerResponse,
    val createdAt: String
)

@Introspected
data class PixKeyDetailsResponse(
    @Enumerated(EnumType.STRING)
    val keyType: TipoChave,
    val key: String,
    val bankAccount: BankAccountResponse,
    val owner: OwnerResponse,
    val createdAt: String
)

@Introspected
data class BankAccountResponse(
    val participant: String,
    val branch: String,
    val accountNumber: String,

    @Enumerated(EnumType.STRING)
    val accountType: TipoContaBacen
)

@Introspected
data class OwnerResponse(
    @Enumerated(EnumType.STRING)
    val type: TipoPessoa,
    val name: String,
    val taxIdNumber: String
)




