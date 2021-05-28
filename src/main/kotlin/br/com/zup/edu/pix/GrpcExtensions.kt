package br.com.zup.edu.pix

import br.com.zup.edu.DeletaChavePixRequest
import br.com.zup.edu.RegistraChavePixRequest
import br.com.zup.edu.TipoChave.*
import br.com.zup.edu.TipoConta.*
import br.com.zup.edu.pix.deleta.DeletaChavePix
import br.com.zup.edu.pix.registra.NovaChavePix
import java.util.*

fun RegistraChavePixRequest.toModel() : NovaChavePix {
    return NovaChavePix(
        clienteId = clientId,
        tipoChave = when(tipoChave) {
            TIPO_DESCONHECIDO -> null
            else -> TipoChave.valueOf(tipoChave.name)
        },
        chave = chave,
        tipoConta = when(tipoConta) {
            CONTA_DESCONHECIDA -> null
            else -> TipoConta.valueOf(tipoConta.name)
        }
    )
}

fun DeletaChavePixRequest.toDelete(): DeletaChavePix {
    return DeletaChavePix(
        pixId = UUID.fromString(pixId),
        clienteId = clientId
    )
}