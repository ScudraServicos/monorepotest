package br.com.ume.api.controllers.transactions.transport.getTransaction.validators

import javax.validation.Constraint
import kotlin.reflect.KClass
import javax.validation.Payload

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Constraint(validatedBy = [GetTransactionRequestValidator::class])
annotation class ValidateGetTransactionRequest(
    val message: String = "TransactionId or Product Name and Id must be sent",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)