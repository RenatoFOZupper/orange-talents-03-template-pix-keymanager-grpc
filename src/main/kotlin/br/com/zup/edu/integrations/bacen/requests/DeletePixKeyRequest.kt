package br.com.zup.edu.integrations.bacen.requests

import io.micronaut.core.annotation.Introspected

@Introspected
data class DeletePixKeyRequest(
    val key: String,
    val participant: String = "60701190"
)