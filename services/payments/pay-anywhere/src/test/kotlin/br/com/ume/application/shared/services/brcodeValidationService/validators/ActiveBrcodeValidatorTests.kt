package br.com.ume.application.shared.services.brcodeValidationService.validators

import br.com.ume.application.features.brcode.inspectBrcode.enums.PixStatusEnum
import br.com.ume.application.features.brcode.inspectBrcode.useCase.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.exceptions.BrcodeValidationException
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.validators.ActiveBrcodeValidator
import br.com.ume.application.shared.testBuilders.PixApiInspectResponseBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ActiveBrcodeValidatorTests {
    private lateinit var activeBrcodeValidator: ActiveBrcodeValidator

    @BeforeEach
    fun setUp() {
        activeBrcodeValidator = ActiveBrcodeValidator()
    }

    @Test
    fun `Should run successfully`() {
        val allowedStatusesSet = setOf(PixStatusEnum.ACTIVE, PixStatusEnum.CREATED)
        allowedStatusesSet.forEach {
            // Given
            val brcode = PixApiInspectResponseBuilder.buildDynamic(status = it)

            // When / Then
            activeBrcodeValidator.execute(brcode)
        }
    }

    @Test
    fun `Should throw if brcode is not active or created`() {
        // Given
        val brcode = PixApiInspectResponseBuilder.buildDynamic()
            .copy(status = PixStatusEnum.PAID)

        // When
        val exception = assertThrows<BrcodeValidationException> {
            activeBrcodeValidator.execute(brcode)
        }

        // Then
        assertEquals(InspectBrcodeErrorEnum.BRCODE_NOT_ACTIVE.toString(), exception.message)
    }
}