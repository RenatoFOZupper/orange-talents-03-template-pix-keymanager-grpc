package br.com.zup.edu.integrations.bacen.responses

import io.micronaut.core.annotation.Introspected

@Introspected
data class DeletePixKeyResponse(
    val key: String,
    val participant: String,
    val deletedAt: String
)