package br.com.zup.edu.pix.deleta

import io.micronaut.core.annotation.Introspected
import java.util.*


@Introspected
data class DeletaChavePix(
    val pixId: UUID,
    val clienteId: String
)
