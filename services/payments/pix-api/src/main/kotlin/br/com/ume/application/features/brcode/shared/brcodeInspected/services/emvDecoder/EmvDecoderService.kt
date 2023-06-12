package br.com.ume.application.features.brcode.shared.brcodeInspected.services.emvDecoder

import br.com.ume.application.features.brcode.shared.brcodeInspected.services.emvDecoder.dtos.DecodedEmvDto

interface EmvDecoderService {
    fun decode(brcode: String): DecodedEmvDto
}