package br.com.ume.application.features.brcode.shared.services.pixApiService.dtos

import br.com.ume.application.features.brcode.inspectBrcode.enums.PixStatusEnum
import br.com.ume.application.features.brcode.inspectBrcode.enums.PixTypeEnum
import java.time.LocalDate
import java.time.LocalDateTime

data class PixApiInspectResponseDto(
    val status: PixStatusEnum,
    val pixType: PixTypeEnum,
    val value: Double,
    val allowAlteration: Boolean,
    val txId: String?,
    val createdAt: LocalDateTime?,
    val presentedAt: LocalDateTime?,
    val expiresAt: LocalDateTime?,
    val dueDate: LocalDate?,
    val withdrawInfo: PixApiInspectWithdrawInfoDto?,
    val changeInfo: PixApiInspectChangeInfoDto?,
    val pixBeneficiary: PixApiInspectBeneficiaryDto
)