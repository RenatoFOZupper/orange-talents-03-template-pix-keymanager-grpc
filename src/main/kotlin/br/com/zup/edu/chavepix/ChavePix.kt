package br.com.zup.edu.chavepix

import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
class ChavePix(
    @field:NotBlank
    val idErpItau: String,

    @field:NotBlank
    @Enumerated(EnumType.STRING)
    val tipoChave: TipoChave,

    @field:NotBlank
    val valorChave: String,

    @field:NotBlank
    @Enumerated(EnumType.STRING)
    val tipoConta: TipoConta
) {
    @Id
    @GeneratedValue
    val id: UUID? = null
}