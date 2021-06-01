package br.com.zup.edu.integrations.bacen.responses

import br.com.zup.edu.integrations.bacen.TipoPessoa
import io.micronaut.core.annotation.Introspected
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Introspected
data class OwnerResponse(
    @Enumerated(EnumType.STRING)
    val type: TipoPessoa,
    val name: String,
    val taxIdNumber: String
)