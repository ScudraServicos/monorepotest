package br.com.ume.application.features.brcode.shared.services.brcodeValidation.validators

import br.com.ume.application.features.brcode.inspectBrcode.useCase.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.PixApiInspectResponseDto
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.exceptions.BrcodeValidationException
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.interfaces.BrcodeValidator
import io.micronaut.runtime.http.scope.RequestScope

@RequestScope
class DenyPixWithdrawValidator: BrcodeValidator {
    override fun execute(brcode: PixApiInspectResponseDto) {
        if (brcode.withdrawInfo != null)
            throw BrcodeValidationException(InspectBrcodeErrorEnum.PIX_WITHDRAW)
    }
}