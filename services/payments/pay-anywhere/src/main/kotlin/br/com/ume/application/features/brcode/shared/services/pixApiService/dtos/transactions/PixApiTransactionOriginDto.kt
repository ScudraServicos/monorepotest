package br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.transactions

data class PixApiTransactionOriginDto(
    val sourceProductReferenceName: String,
    val sourceProductReferenceId: String,
    val userId: String?,
)
