package br.com.zup.edu.pix

import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Introspected
data class NovaChavePix(
    @field:NotBlank
    val clienteId: String?,

    @field:NotNull
    val tipoChave: TipoChave?,

    @field:Size(max = 77)
    val chave: String?,

    @field:NotNull
    val tipoConta: TipoConta?
) {
    fun toModel(conta: ContaAssociada): ChavePix {
        return ChavePix(
            clienteId = UUID.fromString(this.clienteId),
            tipoChave = TipoChave.valueOf(this.tipoChave!!.name),
            chave = if (this.tipoChave == TipoChave.CHAVE_ALEATORIA) UUID.randomUUID().toString() else this.chave!!,
            tipoConta = TipoConta.valueOf(this.tipoConta!!.name),
            conta = conta
        )
    }
}