package br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.dtos

import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.PixStatusEnum

data class BrcodePreviewDto(
    val status: PixStatusEnum,
    val value: Double,
    val allowAlteration: Boolean,
    val txId: String?
)