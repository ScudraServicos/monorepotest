package br.com.ume.application.features.brcode.shared.services.brcodeValidation.validators

import br.com.ume.application.features.brcode.inspectBrcode.enums.PixStatusEnum
import br.com.ume.application.features.brcode.inspectBrcode.useCase.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.exceptions.BrcodeValidationException
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.interfaces.BrcodeValidator
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.PixApiInspectResponseDto
import io.micronaut.runtime.http.scope.RequestScope

@RequestScope
class ActiveBrcodeValidator: BrcodeValidator {

    companion object {
        private val allowedStatusesSet = setOf(PixStatusEnum.ACTIVE, PixStatusEnum.CREATED)
    }

    override fun execute(brcode: PixApiInspectResponseDto) {
        if (!allowedStatusesSet.contains(brcode.status))
            throw BrcodeValidationException(InspectBrcodeErrorEnum.BRCODE_NOT_ACTIVE)
    }
}