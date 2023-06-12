package br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.dtos

import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.InspectBrcodeErrorEnum

data class GetBrcodePreviewOutput(
    val value: BrcodePreviewDto? = null,
    val error: InspectBrcodeErrorEnum? = null
)
