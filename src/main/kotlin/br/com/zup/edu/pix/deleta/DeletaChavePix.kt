package br.com.zup.edu.pix.deleta

import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


@Introspected
data class DeletaChavePix(
    @field:NotBlank(message = "O campo chave não pode estar em branco")
    @field:NotNull(message = "O campo chave não pode estar nulo")
    val pixId: UUID,

    @field:NotBlank(message = "O campo id não pode estar em branco")
    @field:NotNull(message = "O campo id não pode estar nulo")
    val clienteId: String
)
