package br.com.ume.application.shared.services.brcodeValidationService.validators

import br.com.ume.api.configs.BrcodeValidationConfiguration
import br.com.ume.application.features.brcode.inspectBrcode.useCase.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.exceptions.BrcodeValidationException
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.validators.MinimumValueValidator
import br.com.ume.application.shared.testBuilders.PixApiInspectResponseBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito

class MinimumValueValidatorTests {
    private lateinit var brcodeValidationConfigurationMock: BrcodeValidationConfiguration
    private lateinit var minimumValueValidator: MinimumValueValidator

    companion object {
        private const val minimumValue = 10.0
    }

    @BeforeEach
    fun setUp() {
        brcodeValidationConfigurationMock = Mockito.mock(BrcodeValidationConfiguration::class.java)
        Mockito.`when`(brcodeValidationConfigurationMock.minimumValue).thenReturn(minimumValue)

        minimumValueValidator = MinimumValueValidator(brcodeValidationConfigurationMock)
    }

    @Test
    fun `Should run successfully`() {
        // Given
        val brcode = PixApiInspectResponseBuilder.buildDynamic()

        // When / Then
        minimumValueValidator.execute(brcode)
    }

    @Test
    fun `Should throw if value is lower than minimum`() {
        // Given
        val brcode = PixApiInspectResponseBuilder.buildDynamic(value = minimumValue - 1)

        // When
        val exception = assertThrows<BrcodeValidationException> {
            minimumValueValidator.execute(brcode)
        }

        // Then
        assertEquals(InspectBrcodeErrorEnum.MINIMUM_VALUE.toString(), exception.message)
    }
}