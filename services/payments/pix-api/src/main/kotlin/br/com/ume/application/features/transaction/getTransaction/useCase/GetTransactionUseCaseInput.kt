package br.com.ume.application.features.transaction.getTransaction.useCase

data class GetTransactionUseCaseInput(
    val transactionId: String?,
    val sourceProductName: String?,
    val sourceProductId: String?
)