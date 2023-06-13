package br.com.ume.application.features.payments.createPayment.useCase.dtos

data class CreateTransactionEvent(
    val brCode: String,
    val userId: String,
    val sourceProductReferenceId: String,
    val sourceProductReferenceName: String,
)