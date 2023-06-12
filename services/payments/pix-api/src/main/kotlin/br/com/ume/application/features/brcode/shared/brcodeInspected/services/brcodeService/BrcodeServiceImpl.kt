package br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService

import br.com.ume.application.features.brcode.shared.brcodeInspected.helpers.PixStatusHelper
import br.com.ume.application.features.brcode.shared.brcodeInspected.wrappers.bankingPartner.BankingPartnerWrapper
import io.micronaut.runtime.http.scope.RequestScope
import com.starkbank.error.InternalServerError as PartnerInternalServerError
import com.starkbank.error.InputErrors as PartnerInputErrors
import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.dtos.BrcodePreviewDto
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.dtos.DecodeBrcodeOutput
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.dtos.DecodedBrcodeDto
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.dtos.GetBrcodePreviewOutput
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.emvDecoder.EmvDecoderService
import br.com.ume.application.features.brcode.shared.brcodeInspected.wrappers.bankingPartner.utils.parsePartnerAmount
import br.com.ume.application.shared.logging.gcp.JsonLogBuilder
import br.com.ume.application.shared.logging.gcp.LoggerFactory
import java.util.logging.Logger

@RequestScope
class BrcodeServiceImpl(
    private val emvDecoderService: EmvDecoderService,
    private val bankingPartner: BankingPartnerWrapper,
): BrcodeService {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(BrcodeServiceImpl::class.java.name)
    }

    override fun decodeBrcode(brcode: String): DecodeBrcodeOutput {
        val decodedEmv = emvDecoderService.decode(brcode)

        if (decodedEmv.pixKey == null && decodedEmv.merchantUrl == null) {
            return DecodeBrcodeOutput(error = InspectBrcodeErrorEnum.BRCODE_WITHOUT_URL_AND_KEY)
        }

        return DecodeBrcodeOutput(
            value = DecodedBrcodeDto(
                merchantUrl = decodedEmv.merchantUrl,
                pixKey = decodedEmv.pixKey,
            )
        )
    }

    override fun getBrcodePreview(brcode: String): GetBrcodePreviewOutput {
        try {
            val brcodePreview = bankingPartner.getBrcodePreview(brcode)

            return GetBrcodePreviewOutput(
                value = BrcodePreviewDto(
                    status = PixStatusHelper.fromPartnerBrcodePreview(brcodePreview.status),
                    value = parsePartnerAmount(brcodePreview.amount),
                    allowAlteration = brcodePreview.allowChange,
                    txId = brcodePreview.reconciliationId,
                )
            )
        } catch (ex: PartnerInputErrors) {
            log.severe(JsonLogBuilder.build(object {
                val message = InspectBrcodeErrorEnum.BANKING_PARTNER_INVALID_QR_CODE.toString()
                val exception = ex
            }))
            return GetBrcodePreviewOutput(error = InspectBrcodeErrorEnum.BANKING_PARTNER_INVALID_QR_CODE)
        } catch (ex: PartnerInternalServerError) {
            log.severe(JsonLogBuilder.build(object {
                val message = InspectBrcodeErrorEnum.BANKING_PARTNER_ERROR.toString()
                val exception = ex
            }))
            return GetBrcodePreviewOutput(error = InspectBrcodeErrorEnum.BANKING_PARTNER_ERROR)
        }
    }
}