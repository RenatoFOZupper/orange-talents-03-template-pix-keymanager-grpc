package br.com.zup.edu.pix

import br.com.zup.edu.integrations.bacen.TipoChaveBacen
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class TipoChave {

    CPF {
        override fun valida(chave: String?): Boolean {
            return when {
                chave.isNullOrBlank() -> false
                !chave.matches("^[0-9]{11}\$".toRegex()) -> false
                else -> CPFValidator().run {
                    initialize( null)
                    isValid(chave, null)
                }
            }
        }

        override fun converte(): TipoChaveBacen {
            return TipoChaveBacen.CPF
        }
    },
    CELULAR {
        override fun valida(chave: String?): Boolean {
            return when {
                chave.isNullOrBlank() -> false
                else -> chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
            }
        }

        override fun converte(): TipoChaveBacen {
            return TipoChaveBacen.PHONE
        }
    },
    EMAIL {
        override fun valida(chave: String?): Boolean {
            return when {
                chave.isNullOrBlank() -> false
                else -> chave.matches(
                    ("(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c" +
                            "\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:" +
                            "(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]" +
                            "|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]" +
                            "*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09" +
                            "\\x0b\\x0c\\x0e-\\x7f])+)\\])").toRegex())
            }
        }

        override fun converte(): TipoChaveBacen {
            return TipoChaveBacen.EMAIL
        }
    },
    CHAVE_ALEATORIA {
        override fun valida(chave: String?): Boolean {
            return chave.isNullOrBlank()
        }

        override fun converte(): TipoChaveBacen {
            return TipoChaveBacen.RANDOM
        }
    };

    abstract fun valida(chave: String?) : Boolean

    abstract fun converte(): TipoChaveBacen
}