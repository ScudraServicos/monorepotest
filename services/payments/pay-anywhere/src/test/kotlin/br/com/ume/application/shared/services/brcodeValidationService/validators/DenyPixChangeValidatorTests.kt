package br.com.ume.application.shared.services.brcodeValidationService.validators

import br.com.ume.application.features.brcode.inspectBrcode.useCase.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.exceptions.BrcodeValidationException
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.validators.DenyPixChangeValidator
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.PixApiInspectChangeInfoDto
import br.com.ume.application.shared.testBuilders.PixApiInspectResponseBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DenyPixChangeValidatorTests {
    private lateinit var denyPixChangeValidator: DenyPixChangeValidator


    @BeforeEach
    fun setUp() {
        denyPixChangeValidator = DenyPixChangeValidator()
    }

    @Test
    fun `Should run successfully`() {
        // Given
        val brcode = PixApiInspectResponseBuilder.buildDynamic()

        // When / Then
        denyPixChangeValidator.execute(brcode)
    }

    @Test
    fun `Should throw if is pix change`() {
        // Given
        val brcode = PixApiInspectResponseBuilder.buildDynamic()
            .copy(changeInfo = PixApiInspectChangeInfoDto(value = 10.0))

        // When
        val exception = assertThrows<BrcodeValidationException> {
            denyPixChangeValidator.execute(brcode)
        }

        // Then
        assertEquals(InspectBrcodeErrorEnum.PIX_CHANGE.toString(), exception.message)
    }
}