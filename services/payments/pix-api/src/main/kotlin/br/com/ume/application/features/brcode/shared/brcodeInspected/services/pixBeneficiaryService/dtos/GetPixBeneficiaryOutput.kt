package br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.dtos

import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.InspectBrcodeErrorEnum

data class GetPixBeneficiaryOutput(
    val value: PixBeneficiaryDto? = null,
    val error: InspectBrcodeErrorEnum? = null
)
