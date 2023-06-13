package br.com.ume.application.features.brcode.shared.brcodeInspected.domain

import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.PixStatusEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.PixTypeEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.dtos.PixBeneficiaryDto
import java.time.LocalDate
import java.time.LocalDateTime

data class BrcodeInspected(
    val status: PixStatusEnum,
    val pixType: PixTypeEnum,
    val value: Double,
    val allowAlteration: Boolean,
    val txId: String?,
    val createdAt: LocalDateTime?,
    val presentedAt: LocalDateTime?,
    val expiresAt: LocalDateTime?,
    val dueDate: LocalDate?,
    val withdrawInfo: BrcodeInspectedWithdrawInfo?,
    val changeInfo: BrcodeInspectedChangeInfo?,
    val pixBeneficiary: PixBeneficiaryDto
)

