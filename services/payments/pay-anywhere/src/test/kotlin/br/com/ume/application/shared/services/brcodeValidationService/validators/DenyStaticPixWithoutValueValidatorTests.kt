package br.com.ume.application.shared.services.brcodeValidationService.validators

import br.com.ume.application.features.brcode.inspectBrcode.useCase.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.exceptions.BrcodeValidationException
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.validators.DenyStaticPixWithoutValueValidator
import br.com.ume.application.shared.testBuilders.PixApiInspectResponseBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DenyStaticPixWithoutValueValidatorTests {
    private lateinit var denyStaticPixWithoutValueValidator: DenyStaticPixWithoutValueValidator


    @BeforeEach
    fun setUp() {
        denyStaticPixWithoutValueValidator = DenyStaticPixWithoutValueValidator()
    }

    @Test
    fun `Should run successfully`() {
        // Given
        val brcode = PixApiInspectResponseBuilder.buildStatic()

        // When / Then
        denyStaticPixWithoutValueValidator.execute(brcode)
    }

    @Test
    fun `Should throw if is pix static and without value`() {
        // Given
        val brcode = PixApiInspectResponseBuilder.buildStatic(value = 0.0)

        // When
        val exception = assertThrows<BrcodeValidationException> {
            denyStaticPixWithoutValueValidator.execute(brcode)
        }

        // Then
        assertEquals(InspectBrcodeErrorEnum.STATIC_PIX_WITHOUT_VALUE.toString(), exception.message)
    }
}