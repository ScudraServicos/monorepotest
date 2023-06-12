package br.com.ume.application.features.brcode.payBrcode.useCase.dtos

data class TransactionOriginDto(
    val sourceProductReferenceId: String,
    val sourceProductReferenceName: String,
    val userId: String? = null
)
