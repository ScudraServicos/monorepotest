package br.com.ume.application.features.brcode.shared.services.brcodeValidation.validators

import br.com.ume.application.features.brcode.inspectBrcode.useCase.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.exceptions.BrcodeValidationException
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.interfaces.PaymentPermissionValidator
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.PixApiInspectResponseDto
import br.com.ume.application.shared.accessControl.domain.AccessControlUserGroup
import br.com.ume.application.shared.accessControl.gateway.AccessControlGatewayImpl
import br.com.ume.application.shared.accessControl.utils.AccessControlUserGroupRetriever
import io.micronaut.runtime.http.scope.RequestScope

@RequestScope
class PaymentPermissionValidatorImpl(
    private val accessControlGateway: AccessControlGatewayImpl,
    private val accessControlUserGroupRetriever: AccessControlUserGroupRetriever,
): PaymentPermissionValidator {

    override fun execute(brcode: PixApiInspectResponseDto, userId: String) {
        val accessControl = accessControlGateway.getAccessControl(userId)
        if (accessControl == null || accessControl.notAllowed()) {
            throw BrcodeValidationException(InspectBrcodeErrorEnum.PERMISSION_DENIED)
        }
        val document = brcode.pixBeneficiary.document.filter { it.isDigit() }
        accessControl.groups.forEach { groupName ->
            accessControlUserGroupRetriever.get(groupName)?.let { group ->
                if (isAllowed(document, group)) return
            }
        }

        throw BrcodeValidationException(InspectBrcodeErrorEnum.PERMISSION_DENIED)
    }

    private fun isAllowed(document: String, group: AccessControlUserGroup): Boolean {
        return !group.dependsOnStore || group.storeDocumentsSet.contains(document)
    }
}