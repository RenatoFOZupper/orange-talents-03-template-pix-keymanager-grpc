package br.com.zup.edu.pix

import javax.persistence.Embeddable


@Embeddable
class ContaAssociada(
    val instituicao: String,
    val agencia: String,
    val numeroDaConta: String,
    val nomeDoTitular: String,
    val cpfDoTitular: String
) {

}
