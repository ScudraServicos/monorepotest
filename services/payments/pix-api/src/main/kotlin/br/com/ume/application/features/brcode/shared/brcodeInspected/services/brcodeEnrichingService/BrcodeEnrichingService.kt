package br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeEnrichingService

import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeEnrichingService.dtos.GetBrcodePayloadOutput

interface BrcodeEnrichingService {
    fun getBrcodePayload(merchantUrl: String): GetBrcodePayloadOutput
}