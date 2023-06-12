package br.com.ume.api.controllers.pubSub.transport.createTransaction

data class CreateTransactionRequest(
    val brCode: String,
    val userId: String,
    val sourceProductReferenceId: String,
    val sourceProductReferenceName: String,
)
