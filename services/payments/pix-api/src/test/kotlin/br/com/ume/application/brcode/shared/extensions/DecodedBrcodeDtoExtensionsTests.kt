package br.com.ume.application.brcode.shared.extensions

import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.PixTypeEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.extensions.decodedBrcodeDto.getPixType
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.dtos.DecodedBrcodeDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DecodedBrcodeDtoExtensionsTests {
    @Nested
    @DisplayName("getPixType()")
    inner class GetPixType {
        @Test
        fun `Should return DYNAMIC if has merchant url`() {
            // Given
            val decodedBrcodeDto = DecodedBrcodeDto(
                merchantUrl = "url.com/123",
                pixKey = null
            )

            // When
            val pixType = decodedBrcodeDto.getPixType()

            // Then
            assertEquals(PixTypeEnum.DYNAMIC, pixType)
        }

        @Test
        fun `Should return STATIC if has pix key`() {
            // Given
            val decodedBrcodeDto = DecodedBrcodeDto(
                merchantUrl = null,
                pixKey = "123-321"
            )

            // When
            val pixType = decodedBrcodeDto.getPixType()

            // Then
            assertEquals(PixTypeEnum.STATIC, pixType)
        }
    }
}