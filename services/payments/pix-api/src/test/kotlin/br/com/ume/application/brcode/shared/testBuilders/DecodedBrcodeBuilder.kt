package br.com.ume.application.brcode.shared.testBuilders

import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.dtos.DecodedBrcodeDto

class DecodedBrcodeBuilder {
    companion object {
        fun build(merchantUrl: String? = null, pixKey: String? = null): DecodedBrcodeDto {
            return DecodedBrcodeDto(
                merchantUrl,
                pixKey
            )
        }

        fun buildDynamicBrcode(merchantUrl: String = "url.com/123"): DecodedBrcodeDto {
            return DecodedBrcodeDto(
                merchantUrl = merchantUrl,
                pixKey = null
            )
        }

        fun buildStaticBrcode(pixKey: String = "123-321"): DecodedBrcodeDto {
            return DecodedBrcodeDto(
                merchantUrl = null,
                pixKey = pixKey
            )
        }
    }
}