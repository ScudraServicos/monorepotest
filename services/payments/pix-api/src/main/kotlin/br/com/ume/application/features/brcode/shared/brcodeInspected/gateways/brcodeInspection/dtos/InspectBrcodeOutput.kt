package br.com.ume.application.features.brcode.shared.brcodeInspected.gateways.brcodeInspection.dtos

import br.com.ume.application.features.brcode.shared.brcodeInspected.domain.BrcodeInspected
import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.InspectBrcodeErrorEnum

data class InspectBrcodeOutput(
    val brcodeInspected: BrcodeInspected? = null,
    val error: InspectBrcodeErrorEnum? = null
)