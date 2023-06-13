package br.com.ume.application.shared.services.brcodeValidationService

import br.com.ume.application.features.brcode.inspectBrcode.useCase.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.BrcodeValidationServiceImpl
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.BrcodeValidationService
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.exceptions.BrcodeValidationException
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.interfaces.PaymentPermissionValidator
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.validators.*
import br.com.ume.application.shared.testBuilders.PixApiInspectResponseBuilder
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mockito
import org.mockito.Mockito.times

class BrcodeValidationServiceImplTests {
    private lateinit var activeBrcodeValidator: ActiveBrcodeValidator
    private lateinit var denyNaturalPersonBeneficiaryValidator: DenyNaturalPersonBeneficiaryValidator
    private lateinit var denyStaticPixWithoutValueValidator: DenyStaticPixWithoutValueValidator
    private lateinit var denyPixChangeValidator: DenyPixChangeValidator
    private lateinit var denyPixWithdrawValidator: DenyPixWithdrawValidator
    private lateinit var denyValueAlterationValidator: DenyValueAlterationValidator
    private lateinit var minimumValueValidator: MinimumValueValidator
    private lateinit var denyLegalPersonBeneficiaryInBlockListValidator: DenyLegalPersonBeneficiaryInBlockListValidator
    private lateinit var paymentPermissionValidatorMock: PaymentPermissionValidator

    private lateinit var brcodeValidationService: BrcodeValidationService

    @BeforeEach
    fun setUp() {
        activeBrcodeValidator = Mockito.mock(ActiveBrcodeValidator::class.java)
        denyNaturalPersonBeneficiaryValidator = Mockito.mock(DenyNaturalPersonBeneficiaryValidator::class.java)
        denyStaticPixWithoutValueValidator = Mockito.mock(DenyStaticPixWithoutValueValidator::class.java)
        denyPixChangeValidator = Mockito.mock(DenyPixChangeValidator::class.java)
        denyPixWithdrawValidator = Mockito.mock(DenyPixWithdrawValidator::class.java)
        denyValueAlterationValidator = Mockito.mock(DenyValueAlterationValidator::class.java)
        minimumValueValidator = Mockito.mock(MinimumValueValidator::class.java)
        denyLegalPersonBeneficiaryInBlockListValidator = Mockito.mock(DenyLegalPersonBeneficiaryInBlockListValidator::class.java)
        paymentPermissionValidatorMock = Mockito.mock(PaymentPermissionValidator::class.java)

        brcodeValidationService = BrcodeValidationServiceImpl(
            activeBrcodeValidator,
            denyNaturalPersonBeneficiaryValidator,
            denyStaticPixWithoutValueValidator,
            denyPixChangeValidator,
            denyPixWithdrawValidator,
            denyValueAlterationValidator,
            minimumValueValidator,
            denyLegalPersonBeneficiaryInBlockListValidator,
            paymentPermissionValidatorMock,
        )
    }

    companion object {
        private const val defaultUserId = "userId"
    }

    @Nested
    @DisplayName("validate()")
    inner class Validate {
        @Test
        fun `Should run all validation successfully`() {
            // Given
            val brcode = PixApiInspectResponseBuilder.buildDynamic()

            // When
            brcodeValidationService.validate(brcode, defaultUserId)

            // Then
            Mockito.verify(paymentPermissionValidatorMock, times(1)).execute(brcode, defaultUserId)
            Mockito.verify(denyLegalPersonBeneficiaryInBlockListValidator, times(1)).execute(brcode)
            Mockito.verify(activeBrcodeValidator, times(1)).execute(brcode)
            Mockito.verify(denyNaturalPersonBeneficiaryValidator, times(1)).execute(brcode)
            Mockito.verify(minimumValueValidator, times(1)).execute(brcode)
            Mockito.verify(denyStaticPixWithoutValueValidator, times(1)).execute(brcode)
            Mockito.verify(denyPixChangeValidator, times(1)).execute(brcode)
            Mockito.verify(denyPixWithdrawValidator, times(1)).execute(brcode)
            Mockito.verify(denyValueAlterationValidator, times(1)).execute(brcode)
        }

        @Test
        fun `Should throw error if validation fails`() {
            // Given
            val brcode = PixApiInspectResponseBuilder.buildDynamic()
            Mockito.`when`(denyPixChangeValidator.execute(brcode)).thenAnswer {
                throw BrcodeValidationException(InspectBrcodeErrorEnum.PIX_CHANGE)
            }

            // When
            val exception = assertThrows<BrcodeValidationException> {
                brcodeValidationService.validate(brcode, defaultUserId)
            }

            // Then
            assertEquals(InspectBrcodeErrorEnum.PIX_CHANGE.toString(), exception.message)
            Mockito.verify(paymentPermissionValidatorMock, times(1)).execute(brcode, defaultUserId)
            Mockito.verify(denyLegalPersonBeneficiaryInBlockListValidator, times(1)).execute(brcode)
            Mockito.verify(activeBrcodeValidator, times(1)).execute(brcode)
            Mockito.verify(denyNaturalPersonBeneficiaryValidator, times(1)).execute(brcode)
            Mockito.verify(minimumValueValidator, times(1)).execute(brcode)
            Mockito.verify(denyStaticPixWithoutValueValidator, times(1)).execute(brcode)
            Mockito.verify(denyPixChangeValidator, times(1)).execute(brcode)
            Mockito.verify(denyPixWithdrawValidator, times(0)).execute(brcode)
            Mockito.verify(denyValueAlterationValidator, times(0)).execute(brcode)
        }
    }
}