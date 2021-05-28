package br.com.zup.edu.pix.deleta

import br.com.zup.edu.pix.ChavePixRepository
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional


@Singleton
open class DeletaChavePixService(@Inject val repository: ChavePixRepository) {

    @Transactional
    open fun deleta(deletaChavePix: DeletaChavePix) {

        //1.Valida se o cliente possui chave pix cadastrada no sistema

        val chaveDoCliente = repository.findByIdAndClienteId(deletaChavePix.pixId,
            UUID.fromString(deletaChavePix.clienteId))
        repository.delete(chaveDoCliente)


    }

}