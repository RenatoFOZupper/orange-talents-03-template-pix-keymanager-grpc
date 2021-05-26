package br.com.zup.edu.shared.annotations

import br.com.zup.edu.pix.NovaChavePix
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [ValidChavePixValidator::class])
@Target(CLASS, TYPE)
@Retention(RUNTIME)
annotation class ValidChavePix(
    val message: String = "chave Pix inválida (\${validatedValue.tipoChave})",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

@Singleton
class ValidChavePixValidator: ConstraintValidator<ValidChavePix, NovaChavePix> {

    override fun isValid(value: NovaChavePix?, context: ConstraintValidatorContext?): Boolean {
        if (value?.tipoChave == null) {
            return false
        }
       return value.tipoChave.valida(value.chave)
    }


}