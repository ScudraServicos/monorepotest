package br.com.ume.application.features.brcode.inspectBrcode.gateway.inspectBrcode

import br.com.ume.application.features.brcode.inspectBrcode.gateway.inspectBrcode.dtos.InspectBrcodeGatewayOutput
import br.com.ume.application.features.brcode.shared.services.pixApiService.PixApiService
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.PixApiInspectResponseDto
import br.com.ume.application.features.brcode.shared.services.pixApiService.enums.PixApiInspectErrorEnum
import br.com.ume.application.features.brcode.inspectBrcode.useCase.dtos.BeneficiaryDto
import br.com.ume.application.features.brcode.inspectBrcode.useCase.dtos.InspectedBrcodeDto
import br.com.ume.application.features.brcode.inspectBrcode.useCase.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.BrcodeValidationService
import br.com.ume.libs.logging.gcp.JsonLogBuilder
import br.com.ume.libs.logging.gcp.LoggerFactory
import io.micronaut.runtime.http.scope.RequestScope
import java.util.logging.Logger

@RequestScope
class InspectBrcodeGatewayImpl(
    private val pixApiService: PixApiService,
    private val brcodeValidationService: BrcodeValidationService
) : InspectBrcodeGateway {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(InspectBrcodeGatewayImpl::class.java.name)
        private val invalidPixApiErrors = hashSetOf(
            PixApiInspectErrorEnum.BRCODE_WITHOUT_URL_AND_KEY,
            PixApiInspectErrorEnum.BANKING_PARTNER_INVALID_QR_CODE,
            PixApiInspectErrorEnum.BANKING_PARTNER_INVALID_DICT,
            PixApiInspectErrorEnum.BRCODE_PAYLOAD_NOT_AVAILABLE,
            PixApiInspectErrorEnum.BRCODE_PAYLOAD_NOT_VALID,
            PixApiInspectErrorEnum.PIX_KEY_NOT_FOUND,
        )
    }

    override fun inspect(brcode: String, userId: String): InspectBrcodeGatewayOutput {
        val pixApiInspectedBrcode = pixApiService.inspectBrcode(brcode)

        if (pixApiInspectedBrcode.value == null) {
            val error = parsePixApiInspectionError(pixApiInspectedBrcode.error)

            // CAUTION: This log is used in a sink
            log.severe(JsonLogBuilder.build(object {
                val message = "BrcodeInspectionError"
                val error = pixApiInspectedBrcode.error
                val type = error
                val shouldSinkToDatalake = true
            }))
            return InspectBrcodeGatewayOutput(error = error)
        }

        try {
            brcodeValidationService.validate(pixApiInspectedBrcode.value, userId)
            log.info(JsonLogBuilder.build(object {
                val message = "InspectedBrcode"
                val inspectedBrcode = pixApiInspectedBrcode.value
            }))
        } catch (ex: Exception) {
            // CAUTION: This log is used in a sink
            log.severe(JsonLogBuilder.build(object {
                val message = "BrcodeInspectionError"
                val error = ex.message
                val type = "BUSINESS_RULE"
                val inspectedBrcode = pixApiInspectedBrcode.value
                val shouldSinkToDatalake = true
            }))
            throw ex
        }

        val value = parsePixApiInspectedBrcode(pixApiInspectedBrcode.value)
        return InspectBrcodeGatewayOutput(value)
    }

    private fun parsePixApiInspectionError(error: PixApiInspectErrorEnum?): InspectBrcodeErrorEnum {
        return when (error) {
            in invalidPixApiErrors -> InspectBrcodeErrorEnum.INVALID
            else -> InspectBrcodeErrorEnum.ERROR
        }
    }

    private fun parsePixApiInspectedBrcode(pixApiInspectResponse: PixApiInspectResponseDto): InspectedBrcodeDto {
        return InspectedBrcodeDto(
            value = pixApiInspectResponse.value,
            txId = pixApiInspectResponse.txId,
            expiresAt = pixApiInspectResponse.expiresAt,
            beneficiary = BeneficiaryDto(
                name = pixApiInspectResponse.pixBeneficiary.name,
                document = pixApiInspectResponse.pixBeneficiary.document,
                bankCode = pixApiInspectResponse.pixBeneficiary.bankingAccount.bankCode,
                bankName = pixApiInspectResponse.pixBeneficiary.bankingAccount.bankName,
                pixKey = pixApiInspectResponse.pixBeneficiary.pixKey,
            ),
        )
    }
}