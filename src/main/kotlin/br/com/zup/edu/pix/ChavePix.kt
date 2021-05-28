package br.com.zup.edu.pix

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Table(uniqueConstraints = [UniqueConstraint(
    name = "uk_chave_pix",
    columnNames = ["chave"],
)])
class ChavePix(
    @field:NotNull
    @Column(nullable = false)
    val clienteId: UUID,

    @field:NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val tipoChave: TipoChave,

    @field:NotBlank
    @Column(nullable = false)
    var chave: String,

    @field:NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val tipoConta: TipoConta,

    @Embedded
    val conta: ContaAssociada
) {

    @Id
    @GeneratedValue
    val id: UUID? = null

    val criadaEm: LocalDateTime = LocalDateTime.now()

}
