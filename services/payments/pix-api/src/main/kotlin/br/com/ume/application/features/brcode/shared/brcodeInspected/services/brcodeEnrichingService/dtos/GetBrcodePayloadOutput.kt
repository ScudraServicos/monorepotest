package br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeEnrichingService.dtos

import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.InspectBrcodeErrorEnum

data class GetBrcodePayloadOutput(
    val value: BrcodePayloadDto? = null,
    val error: InspectBrcodeErrorEnum? = null
)
