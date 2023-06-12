package br.com.ume.application.shared.services.brcodeValidationService.validators

import br.com.ume.application.features.brcode.inspectBrcode.useCase.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.exceptions.BrcodeValidationException
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.validators.DenyPixWithdrawValidator
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.PixApiInspectWithdrawInfoDto
import br.com.ume.application.shared.testBuilders.PixApiInspectResponseBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DenyPixWithdrawValidatorTests {
    private lateinit var denyPixWithdrawValidator: DenyPixWithdrawValidator


    @BeforeEach
    fun setUp() {
        denyPixWithdrawValidator = DenyPixWithdrawValidator()
    }

    @Test
    fun `Should run successfully`() {
        // Given
        val brcode = PixApiInspectResponseBuilder.buildDynamic()

        // When / Then
        denyPixWithdrawValidator.execute(brcode)
    }

    @Test
    fun `Should throw if is pix withdraw`() {
        // Given
        val brcode = PixApiInspectResponseBuilder.buildDynamic()
            .copy(withdrawInfo = PixApiInspectWithdrawInfoDto(value = 10.0))

        // When
        val exception = assertThrows<BrcodeValidationException> {
            denyPixWithdrawValidator.execute(brcode)
        }

        // Then
        assertEquals(InspectBrcodeErrorEnum.PIX_WITHDRAW.toString(), exception.message)
    }
}