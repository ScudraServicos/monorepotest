package br.com.ume.application.features.brcode.inspectBrcode.gateway.inspectBrcode.dtos

import br.com.ume.application.features.brcode.inspectBrcode.useCase.dtos.InspectedBrcodeDto
import br.com.ume.application.features.brcode.inspectBrcode.useCase.enums.InspectBrcodeErrorEnum

data class InspectBrcodeGatewayOutput(
    val value: InspectedBrcodeDto? = null,
    val error: InspectBrcodeErrorEnum? = null
)
