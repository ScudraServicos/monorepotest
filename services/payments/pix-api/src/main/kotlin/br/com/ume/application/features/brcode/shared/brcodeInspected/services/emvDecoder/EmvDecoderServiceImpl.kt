package br.com.ume.application.features.brcode.shared.brcodeInspected.services.emvDecoder

import br.com.ume.application.features.brcode.shared.brcodeInspected.services.emvDecoder.dtos.DecodedEmvDto
import com.emv.qrcode.decoder.mpm.DecoderMpm
import com.emv.qrcode.model.mpm.MerchantAccountInformationReservedAdditional
import com.emv.qrcode.model.mpm.MerchantPresentedMode
import io.micronaut.runtime.http.scope.RequestScope

@RequestScope
class EmvDecoderServiceImpl : EmvDecoderService {
    companion object {
        private const val merchantAccountInformationTag: String = "26"
        private const val merchantPixKeySubTag: String = "01"
        private const val merchantUrlSubTag: String = "25"
    }

    override fun decode(brcode: String): DecodedEmvDto {
        val decodedEmv = DecoderMpm.decode(
            brcode,
            MerchantPresentedMode::class.java
        )

        val merchantAccountInformation = decodedEmv.merchantAccountInformation[merchantAccountInformationTag]?.value as MerchantAccountInformationReservedAdditional?

        val pixKey = extractPixKey(merchantAccountInformation)
        val merchantUrl = extractMerchantUrl(merchantAccountInformation)

        return DecodedEmvDto(
            merchantUrl,
            pixKey
        )
    }

    private fun extractPixKey(merchantAccountInformation: MerchantAccountInformationReservedAdditional?): String? {
        return merchantAccountInformation?.paymentNetworkSpecific?.get(merchantPixKeySubTag)?.value
    }

    private fun extractMerchantUrl(merchantAccountInformation: MerchantAccountInformationReservedAdditional?): String? {
        return merchantAccountInformation?.paymentNetworkSpecific?.get(merchantUrlSubTag)?.value
    }
}