package br.com.zup.edu.pix

import br.com.zup.edu.integrations.bacen.TipoContaBacen

enum class TipoConta {

    CONTA_CORRENTE {

        override fun converteTipoContaBacen(): TipoContaBacen {
            return TipoContaBacen.CACC
        }
    },
    CONTA_POUPANCA {
        override fun converteTipoContaBacen(): TipoContaBacen {
            return TipoContaBacen.SVGS
        }
    };

    abstract fun converteTipoContaBacen(): TipoContaBacen
}

