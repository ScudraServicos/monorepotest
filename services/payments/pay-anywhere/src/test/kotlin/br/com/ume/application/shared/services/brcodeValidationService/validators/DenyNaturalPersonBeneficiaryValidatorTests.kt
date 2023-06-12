package br.com.ume.application.shared.services.brcodeValidationService.validators

import br.com.ume.application.features.brcode.inspectBrcode.useCase.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.exceptions.BrcodeValidationException
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.validators.DenyNaturalPersonBeneficiaryValidator
import br.com.ume.application.shared.testBuilders.PixApiInspectBeneficiaryBuilder
import br.com.ume.application.shared.testBuilders.PixApiInspectResponseBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DenyNaturalPersonBeneficiaryValidatorTests {
    private lateinit var denyNaturalPersonBeneficiaryValidator: DenyNaturalPersonBeneficiaryValidator


    @BeforeEach
    fun setUp() {
        denyNaturalPersonBeneficiaryValidator = DenyNaturalPersonBeneficiaryValidator()
    }

    @Test
    fun `Should run successfully`() {
        // Given
        val brcode = PixApiInspectResponseBuilder.buildDynamic()
            .copy(pixBeneficiary = PixApiInspectBeneficiaryBuilder.buildLegalPerson())

        // When / Then
        denyNaturalPersonBeneficiaryValidator.execute(brcode)
    }

    @Test
    fun `Should throw if beneficiary is natural person`() {
        // Given
        val brcode = PixApiInspectResponseBuilder.buildDynamic()
            .copy(pixBeneficiary = PixApiInspectBeneficiaryBuilder.buildNaturalPerson())

        // When
        val exception = assertThrows<BrcodeValidationException> {
            denyNaturalPersonBeneficiaryValidator.execute(brcode)
        }

        // Then
        assertEquals(InspectBrcodeErrorEnum.NATURAL_PERSON_BENEFICIARY.toString(), exception.message)
    }
}