package br.com.ume.application.shared.services.brcodeValidationService.validators

import br.com.ume.application.features.brcode.inspectBrcode.useCase.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.exceptions.BrcodeValidationException
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.validators.DenyValueAlterationValidator
import br.com.ume.application.shared.testBuilders.PixApiInspectResponseBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DenyValueAlterationValidatorTests {
    private lateinit var denyValueAlterationValidator: DenyValueAlterationValidator

    @BeforeEach
    fun setUp() {
        denyValueAlterationValidator = DenyValueAlterationValidator()
    }

    @Test
    fun `Should run successfully`() {
        // Given
        val brcode = PixApiInspectResponseBuilder.buildDynamic()

        // When / Then
        denyValueAlterationValidator.execute(brcode)
    }

    @Test
    fun `Should throw if allows value alteration`() {
        // Given
        val brcode = PixApiInspectResponseBuilder.buildDynamic(allowAlteration = true)

        // When
        val exception = assertThrows<BrcodeValidationException> {
            denyValueAlterationValidator.execute(brcode)
        }

        // Then
        assertEquals(InspectBrcodeErrorEnum.ALLOW_ALTERATION.toString(), exception.message)
    }
}