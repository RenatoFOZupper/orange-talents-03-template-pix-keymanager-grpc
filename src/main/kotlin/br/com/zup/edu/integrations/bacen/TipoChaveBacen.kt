package br.com.zup.edu.integrations.bacen

import br.com.zup.edu.pix.TipoChave

enum class TipoChaveBacen {

    CPF {

        override fun converte(): TipoChave {
            return TipoChave.CPF
        }
    },
    CNPJ {

        override fun converte(): TipoChave {
            TODO()
        }
    },
    PHONE {

        override fun converte(): TipoChave {
            return TipoChave.CELULAR
        }
    },
    EMAIL {

        override fun converte(): TipoChave {
            return TipoChave.EMAIL
        }
    },
    RANDOM {

        override fun converte(): TipoChave {
            return TipoChave.CHAVE_ALEATORIA
        }
    };


    abstract fun converte(): TipoChave
}