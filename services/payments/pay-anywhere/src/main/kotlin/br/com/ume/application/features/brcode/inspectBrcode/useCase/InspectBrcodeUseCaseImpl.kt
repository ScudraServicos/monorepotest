package br.com.ume.application.features.brcode.inspectBrcode.useCase

import br.com.ume.api.exceptions.types.BusinessRuleException
import br.com.ume.api.exceptions.types.InternalErrorException
import br.com.ume.application.features.brcode.inspectBrcode.gateway.inspectBrcode.InspectBrcodeGateway
import br.com.ume.application.features.brcode.inspectBrcode.useCase.dtos.InspectedBrcodeDto
import br.com.ume.application.features.brcode.inspectBrcode.useCase.enums.InspectBrcodeErrorEnum
import io.micronaut.runtime.http.scope.RequestScope

@RequestScope
class InspectBrcodeUseCaseImpl(
    private val inspectBrcodeGateway: InspectBrcodeGateway
): InspectBrcodeUseCase {
    companion object {
        private val businessRuleErrors = hashSetOf(
            InspectBrcodeErrorEnum.INVALID,
        )
    }

    override fun execute(brcode: String, userId: String): InspectedBrcodeDto {
        val output = inspectBrcodeGateway.inspect(brcode, userId)
        if (output.value == null) throw getBrcodeInspectionException(output.error)

        return output.value
    }

    private fun getBrcodeInspectionException(error: InspectBrcodeErrorEnum?): Exception {
        return when (error) {
            in businessRuleErrors -> BusinessRuleException(error.toString())
            else -> InternalErrorException(error.toString())
        }
    }
}