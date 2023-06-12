package br.com.ume.application.brcode.shared.testBuilders

import br.com.ume.application.features.brcode.shared.brcodeInspected.services.emvDecoder.dtos.DecodedEmvDto

class DecodedEmvBuilder {
    companion object {
        fun build(merchantUrl: String? = null, pixKey: String? = null): DecodedEmvDto {
            return DecodedEmvDto(
                merchantUrl,
                pixKey
            )
        }
    }
}