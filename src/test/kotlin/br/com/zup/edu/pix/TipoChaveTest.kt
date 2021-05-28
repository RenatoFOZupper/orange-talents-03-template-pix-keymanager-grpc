package br.com.zup.edu.pix

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class TipoChaveTest {

    @Nested
    inner class CHAVE_ALEATORIA {

        @Test
        fun `nao deve ser valido quando chave aleatoria for nula ou vazia`() {
            with(TipoChave.CHAVE_ALEATORIA) {
                assertTrue(valida(null))
                assertTrue(valida(""))
            }
        }

        @Test
        fun `nao deve ser valido quando chave aleatoria possuir um valor`() {
            with(TipoChave.CHAVE_ALEATORIA) {
                assertFalse(valida("03385691211"))
                assertFalse(valida("teste@teste.com.br") )
            }
        }
    }

    @Nested
    inner class CPF {

        @Test
        fun `nao deve ser valido quando cpf for nulo ou vazio`() {
            with(TipoChave.CPF) {
                assertFalse(valida(null))
                assertFalse(valida(""))
            }
        }

        @Test
        fun `nao deve ser valido quando formato nao seguir o padrao de onze digitos`() {
            with(TipoChave.CPF) {
                assertFalse(valida("02A677810XX"))
                assertFalse(valida("024.677.810-54"))
            }
        }

        @Test
        fun `deve ser valido apenas o formato de onze digitos seguidos`() {
            with(TipoChave.CPF) {
                assertTrue(valida("02467781054"))
            }
        }
    }



    @Nested
    inner class CELULAR {

        @Test
        fun `nao deve ser valido quando o numero for nulo ou vazio`() {
            with(TipoChave.CELULAR) {
                assertFalse(valida(null))
                assertFalse(valida(""))
            }
        }

        @Test
        fun `deve ser valido quando for um numero valido`() {
            with(TipoChave.CELULAR) {
                assertTrue(valida("+5523963401698"))
            }
        }

        @Test
        fun `nao deve validar quando for um numero invalido`() {
            with(TipoChave.CELULAR) {
                assertFalse(valida("99874-1325"))
                assertFalse(valida("+5511a90817789"))
            }
        }
    }

    @Nested
    inner class EMAIL {

        @Test
        fun `nao deve validar quando email for nulo ou vazio`() {
            with(TipoChave.EMAIL) {
                assertFalse(valida(null))
                assertFalse(valida(""))
            }
        }

        @Test
        fun `nao deve validar quando email for no formato invalido`() {
            with(TipoChave.EMAIL) {
                assertFalse(valida("zup.eduzup.com.br"))
                assertFalse(valida("zup.edu@zup.com."))
            }
        }

        @Test
        fun `deve validar quando email for no formato esperado`() {
            with(TipoChave.EMAIL) {
                assertTrue(valida("teste.zup@gmail.com.br"))
            }
        }

    }



}