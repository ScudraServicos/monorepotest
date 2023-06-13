package br.com.ume.application.features.brcode.inspectBrcode.useCase

import br.com.ume.api.exceptions.types.BusinessRuleException
import br.com.ume.api.exceptions.types.InternalErrorException
import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.gateways.brcodeInspection.BrcodeInspectionGateway
import br.com.ume.application.features.brcode.shared.brcodeInspected.domain.BrcodeInspected
import br.com.ume.application.shared.logging.gcp.JsonLogBuilder
import br.com.ume.application.shared.logging.gcp.LoggerFactory
import io.micronaut.runtime.http.scope.RequestScope
import java.util.logging.Logger

@RequestScope
class InspectBrcodeUseCaseImpl(
    private val brcodeInspectionGateway: BrcodeInspectionGateway
): InspectBrcodeUseCase {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(InspectBrcodeUseCaseImpl::class.java.name)
        private val businessRuleErrors = listOf(
            InspectBrcodeErrorEnum.BRCODE_WITHOUT_URL_AND_KEY,
            InspectBrcodeErrorEnum.BANKING_PARTNER_INVALID_QR_CODE,
            InspectBrcodeErrorEnum.BRCODE_PAYLOAD_NOT_AVAILABLE,
            InspectBrcodeErrorEnum.BRCODE_PAYLOAD_NOT_VALID,
            InspectBrcodeErrorEnum.BRCODE_PAYLOAD_DECODING_ERROR,
            InspectBrcodeErrorEnum.BANKING_PARTNER_INVALID_DICT,
        )
    }

    override fun execute(brcode: String): BrcodeInspected {
        val inspectedBrcode = brcodeInspectionGateway.inspectBrcode(brcode)
        if (inspectedBrcode.brcodeInspected == null) {
            log.severe(JsonLogBuilder.build(object {
                val message = "InspectBrcodeError"
                val error = inspectedBrcode.error.toString()
            }))
            throw getBrcodeInspectionException(inspectedBrcode.error)
        }

        return inspectedBrcode.brcodeInspected
    }

    private fun getBrcodeInspectionException(error: InspectBrcodeErrorEnum?): Exception {
        return when (error) {
            in businessRuleErrors -> BusinessRuleException(error.toString())
            else -> InternalErrorException(error.toString())
        }
    }
}
