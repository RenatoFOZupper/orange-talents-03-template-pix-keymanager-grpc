package br.com.zup.edu.integrations.bacen

import br.com.zup.edu.integrations.bacen.requests.CreatePixKeyRequest
import br.com.zup.edu.integrations.bacen.requests.DeletePixKeyRequest
import br.com.zup.edu.integrations.bacen.responses.CreatePixKeyResponse
import br.com.zup.edu.integrations.bacen.responses.DeletePixKeyResponse
import br.com.zup.edu.integrations.bacen.responses.PixKeyDetailsResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType.APPLICATION_XML
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Consumes(APPLICATION_XML)
@Produces(APPLICATION_XML)
@Client("\${bacen.contas.url}")
interface ContasDeClientesNoBacenClient {

    @Get("/api/v1/pix/keys/{key}")
    fun buscaPorChavePix(@PathVariable key: String): HttpResponse<PixKeyDetailsResponse>

    @Post("/api/v1/pix/keys")
    fun registraNovaChavePix(@Body request: CreatePixKeyRequest): HttpResponse<CreatePixKeyResponse>

    @Delete("/api/v1/pix/keys/{key}")
    fun deletaChavePix(@PathVariable key: String, @Body request: DeletePixKeyRequest) : HttpResponse<DeletePixKeyResponse>

}













