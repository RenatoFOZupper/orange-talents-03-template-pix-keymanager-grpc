package br.com.zup.edu.integrations.bacen.requests


import br.com.zup.edu.integrations.bacen.TipoChaveBacen
import br.com.zup.edu.integrations.bacen.TipoPessoa
import br.com.zup.edu.integrations.bacen.responses.BankAccountResponse
import br.com.zup.edu.integrations.bacen.responses.OwnerResponse
import br.com.zup.edu.pix.ChavePix
import br.com.zup.edu.pix.TipoChave
import io.micronaut.core.annotation.Introspected
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Introspected
data class CreatePixKeyRequest(
    @Enumerated(EnumType.STRING)
    val keyType: TipoChaveBacen,
    var key: String,
    val bankAccount: BankAccountResponse,
    val owner: OwnerResponse
) {

    companion object {

        fun toModelBacen(chave: ChavePix) : CreatePixKeyRequest {
            return CreatePixKeyRequest(
                keyType = chave.tipoChave.converte(),
                key = chave.chave,
                bankAccount = BankAccountResponse(
                    participant = "60701190",
                    branch = chave.conta.agencia,
                    accountNumber = chave.conta.numeroDaConta,
                    accountType = chave.tipoConta.converteTipoContaBacen()
                ),
                owner = OwnerResponse(
                    type = TipoPessoa.NATURAL_PERSON,
                    name = chave.conta.nomeDoTitular,
                    taxIdNumber = chave.conta.cpfDoTitular
                )
            )
        }

    }



}