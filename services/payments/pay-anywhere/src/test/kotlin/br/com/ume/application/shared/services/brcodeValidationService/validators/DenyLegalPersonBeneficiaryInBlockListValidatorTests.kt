package br.com.ume.application.shared.services.brcodeValidationService.validators

import br.com.ume.api.configs.BrcodeValidationConfiguration
import br.com.ume.application.features.brcode.inspectBrcode.useCase.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.exceptions.BrcodeValidationException
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.validators.DenyLegalPersonBeneficiaryInBlockListValidator
import br.com.ume.application.shared.testBuilders.PixApiInspectBeneficiaryBuilder
import br.com.ume.application.shared.testBuilders.PixApiInspectResponseBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito

class DenyLegalPersonBeneficiaryInBlockListValidatorTests {
    private lateinit var brcodeValidationConfigurationMock: BrcodeValidationConfiguration
    private lateinit var denyLegalPersonBeneficiaryInBlockListValidator: DenyLegalPersonBeneficiaryInBlockListValidator

    companion object {
        private const val legalPersonInBlockList = "36.279.336/0001-82"
    }

    @BeforeEach
    fun setUp() {
        brcodeValidationConfigurationMock = Mockito.mock(BrcodeValidationConfiguration::class.java)
        Mockito.`when`(brcodeValidationConfigurationMock.legalPersonBlockList).thenReturn(hashSetOf(legalPersonInBlockList))

        denyLegalPersonBeneficiaryInBlockListValidator = DenyLegalPersonBeneficiaryInBlockListValidator(brcodeValidationConfigurationMock)
    }

    @Test
    fun `Should run successfully if beneficiary is natural person`() {
        // Given
        val brcode = PixApiInspectResponseBuilder.buildDynamic()
            .copy(pixBeneficiary = PixApiInspectBeneficiaryBuilder.buildNaturalPerson())

        // When / Then
        denyLegalPersonBeneficiaryInBlockListValidator.execute(brcode)
    }

    @Test
    fun `Should run successfully if legal person beneficiary is not in block list`() {
        // Given
        val brcode = PixApiInspectResponseBuilder.buildDynamic()

        // When / Then
        denyLegalPersonBeneficiaryInBlockListValidator.execute(brcode)
    }

    @Test
    fun `Should throw if legal person beneficiary is in block list`() {
        // Given
        val brcode = PixApiInspectResponseBuilder.buildDynamic()
            .copy(pixBeneficiary = PixApiInspectBeneficiaryBuilder.buildLegalPerson().copy(document = legalPersonInBlockList))

        // When
        val exception = assertThrows<BrcodeValidationException> {
            denyLegalPersonBeneficiaryInBlockListValidator.execute(brcode)
        }

        // Then
        assertEquals(InspectBrcodeErrorEnum.LEGAL_PERSON_BENEFICIARY_IN_BLOCK_LIST.toString(), exception.message)
    }
}