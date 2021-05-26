package br.com.zup.edu.pix

import br.com.zup.edu.RegistraChavePixRequest
import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta

fun RegistraChavePixRequest.toModel() : NovaChavePix {
    return NovaChavePix(
        clienteId = clientId,
        tipoChave = when(tipoChave) {
            TipoChave.TIPO_DESCONHECIDO -> null
            else -> TipoChave.valueOf(tipoChave.name)
        },
        chave = chave,
        tipoConta = when(tipoConta) {
            TipoConta.CONTA_DESCONHECIDA -> null
            else -> TipoConta.valueOf(tipoConta.name)
        }
    )
}