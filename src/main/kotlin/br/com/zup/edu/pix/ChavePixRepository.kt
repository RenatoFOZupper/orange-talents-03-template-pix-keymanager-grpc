package br.com.zup.edu.pix

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository: JpaRepository<ChavePix, UUID> {

    fun existsByChave(chave: String?): Boolean
    fun existsByClienteId(clienteId: UUID?): Boolean
    fun findByIdAndClienteId(pixId: UUID, clienteId: UUID): Optional<ChavePix>
}