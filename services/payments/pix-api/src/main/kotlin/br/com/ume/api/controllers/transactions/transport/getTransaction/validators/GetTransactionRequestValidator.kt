package br.com.ume.api.controllers.transactions.transport.getTransaction.validators

import br.com.ume.api.controllers.transactions.transport.getTransaction.GetTransactionRequest
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import jakarta.inject.Singleton

@Singleton
class GetTransactionRequestValidator : ConstraintValidator<ValidateGetTransactionRequest, GetTransactionRequest> {
    override fun isValid(
        transactionRequest: GetTransactionRequest,
        annotationMetadata: AnnotationValue<ValidateGetTransactionRequest>,
        context: ConstraintValidatorContext
    ): Boolean {
        return transactionRequest.transactionId != null ||
                (!transactionRequest.sourceProductName.isNullOrBlank() && !transactionRequest.sourceProductId.isNullOrBlank())
    }
}