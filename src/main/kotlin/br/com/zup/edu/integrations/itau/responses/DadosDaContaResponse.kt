package br.com.zup.edu.integrations.itau.responses

import br.com.zup.edu.pix.ContaAssociada

data class DadosDaContaResponse(
    val tipo: String,
    val instituicao: InstituicaoResponse,
    val agencia: String,
    val numero: String,
    val titular: TitularResponse
) {
    fun toModel(): ContaAssociada {
        return ContaAssociada(
                instituicao = this.instituicao.nome,
                nomeDoTitular = this.titular.nome,
                cpfDoTitular = this.titular.cpf,
                agencia = this.agencia,
                numeroDaConta = this.numero
        )
    }

}



