package br.com.ume.application.shared.transaction.gateway.dtos

data class CreateTransactionOriginDto(
    val sourceProductReferenceName: String,
    val sourceProductReferenceId: String,
    val userId: String?,
)
