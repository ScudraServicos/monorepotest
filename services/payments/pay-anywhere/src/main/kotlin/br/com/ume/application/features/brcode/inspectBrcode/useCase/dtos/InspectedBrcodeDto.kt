package br.com.ume.application.features.brcode.inspectBrcode.useCase.dtos

import java.time.LocalDateTime

data class InspectedBrcodeDto(
    val value: Double,
    val txId: String?,
    val expiresAt: LocalDateTime?,
    val beneficiary: BeneficiaryDto
)
