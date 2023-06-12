package br.com.ume.application.features.brcode.shared.services.brcodeValidation

import br.com.ume.application.features.brcode.shared.services.brcodeValidation.interfaces.BrcodeValidator
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.interfaces.PaymentPermissionValidator
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.validators.*
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.PixApiInspectResponseDto
import io.micronaut.runtime.http.scope.RequestScope

@RequestScope
class BrcodeValidationServiceImpl(
    activeBrcodeValidator: ActiveBrcodeValidator,
    denyNaturalPersonBeneficiaryValidator: DenyNaturalPersonBeneficiaryValidator,
    denyStaticPixWithoutValueValidator: DenyStaticPixWithoutValueValidator,
    denyPixChangeValidator: DenyPixChangeValidator,
    denyPixWithdrawValidator: DenyPixWithdrawValidator,
    denyValueAlterationValidator: DenyValueAlterationValidator,
    minimumValueValidator: MinimumValueValidator,
    denyLegalPersonBeneficiaryInBlockListValidator: DenyLegalPersonBeneficiaryInBlockListValidator,
    private val paymentPermissionValidator: PaymentPermissionValidator,
) : BrcodeValidationService {
    private val validators: List<BrcodeValidator> = listOf(
        denyLegalPersonBeneficiaryInBlockListValidator,
        activeBrcodeValidator,
        denyNaturalPersonBeneficiaryValidator,
        minimumValueValidator,
        denyStaticPixWithoutValueValidator,
        denyPixChangeValidator,
        denyPixWithdrawValidator,
        denyValueAlterationValidator,
    )

    override fun validate(brcode: PixApiInspectResponseDto, userId: String) {
        paymentPermissionValidator.execute(brcode, userId)

        validators.forEach { it.execute(brcode) }
    }
}