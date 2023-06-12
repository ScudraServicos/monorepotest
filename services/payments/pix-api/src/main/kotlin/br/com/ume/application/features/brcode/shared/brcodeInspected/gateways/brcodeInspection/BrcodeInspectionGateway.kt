package br.com.ume.application.features.brcode.shared.brcodeInspected.gateways.brcodeInspection

import br.com.ume.application.features.brcode.shared.brcodeInspected.gateways.brcodeInspection.dtos.InspectBrcodeOutput

interface BrcodeInspectionGateway {
    fun inspectBrcode(brcode: String): InspectBrcodeOutput
}