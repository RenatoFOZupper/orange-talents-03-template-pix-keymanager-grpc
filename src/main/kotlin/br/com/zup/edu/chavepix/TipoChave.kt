package br.com.zup.edu.chavepix

enum class TipoChave {

    CPF {
        override fun valida(value: String): Boolean {
            if(value.isNullOrBlank()) return false

            return value.matches(Regex("^[0-9]{11}\$"))
        }
    },
    CELULAR {
        override fun valida(value: String): Boolean {
            if (value.isNullOrBlank()) return false

            return value.matches(Regex("^\\+[1-9][0-9]\\d{1,14}\$"))
        }
    },
    EMAIL {
        override fun valida(value: String): Boolean {
            if(value.isNullOrBlank()) return false

            return value.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)\$"))
        }
    },
    CHAVE_ALEATORIA {
        override fun valida(value: String): Boolean {
            return value.isNullOrBlank()
        }
    };

    abstract fun valida(value: String) : Boolean
}

