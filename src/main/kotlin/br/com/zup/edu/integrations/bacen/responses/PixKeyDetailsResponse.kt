package br.com.zup.edu.integrations.bacen.responses

import br.com.zup.edu.integrations.bacen.TipoChaveBacen
import br.com.zup.edu.integrations.bacen.responses.BankAccountResponse
import br.com.zup.edu.integrations.bacen.responses.OwnerResponse
import br.com.zup.edu.pix.TipoChave
import io.micronaut.core.annotation.Introspected
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Introspected
data class PixKeyDetailsResponse(
    @Enumerated(EnumType.STRING)
    val keyType: TipoChaveBacen,
    val key: String,
    val bankAccount: BankAccountResponse,
    val owner: OwnerResponse,
    val createdAt: String
)