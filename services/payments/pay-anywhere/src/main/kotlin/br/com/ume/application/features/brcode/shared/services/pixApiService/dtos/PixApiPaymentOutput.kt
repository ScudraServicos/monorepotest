package br.com.ume.application.features.brcode.shared.services.pixApiService.dtos

data class PixApiPaymentOutput(
    val value: PixApiPaymentResponseDto? = null,
    val error: String? = null
)
