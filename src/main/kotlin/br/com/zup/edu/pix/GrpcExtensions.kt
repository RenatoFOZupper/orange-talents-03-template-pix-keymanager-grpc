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

//fun ChavePix.toModelBacen() : CreatePixKeyRequest {
//    return CreatePixKeyRequest(
//        keyType = this.tipoChave.converte(),
//        key = this.chave,
//        bankAccount = BankAccountResponse(
//            participant = "60701190",
//            branch = this.conta.agencia,
//            accountNumber = this.conta.numeroDaConta,
//            accountType = this.tipoConta.converteTipoContaBacen()
//        ),
//        owner = OwnerResponse(
//            type = TipoPessoa.NATURAL_PERSON,
//            name = this.conta.nomeDoTitular,
//            taxIdNumber = this.conta.cpfDoTitular
//        )
//    )
//}