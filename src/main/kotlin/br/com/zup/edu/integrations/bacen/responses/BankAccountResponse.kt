package br.com.zup.edu.integrations.bacen.responses

import br.com.zup.edu.integrations.bacen.TipoContaBacen
import io.micronaut.core.annotation.Introspected
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Introspected
data class BankAccountResponse(
    val participant: String,
    val branch: String,
    val accountNumber: String,

    @Enumerated(EnumType.STRING)
    val accountType: TipoContaBacen
)