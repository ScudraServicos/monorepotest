package br.com.ume.application.features.brcode.shared.services.pixApiService.dtos

import br.com.ume.application.shared.enums.PixPaymentStatusEnum

data class PixApiPaymentResponseDto(
    val status: PixPaymentStatusEnum
)
