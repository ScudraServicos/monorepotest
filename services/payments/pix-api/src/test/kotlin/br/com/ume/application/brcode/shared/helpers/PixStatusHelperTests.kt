package br.com.ume.application.brcode.shared.helpers

import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.PixStatusEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.helpers.PixStatusHelper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PixStatusHelperTests {
    @Nested
    @DisplayName("fromPartnerBrcodePreview()")
    inner class FromPartnerBrcodePreview {
        @Test
        fun `Should return ACTIVE`() {
            // Given
            val bankingPartnerBrcodeStatus = "active"

            // When
            val pixStatus = PixStatusHelper.fromPartnerBrcodePreview(bankingPartnerBrcodeStatus)

            // Then
            val expectedResult = PixStatusEnum.ACTIVE
            assertEquals(expectedResult, pixStatus)
        }

        @Test
        fun `Should return PAID`() {
            // Given
            val bankingPartnerBrcodeStatus = "paid"

            // When
            val pixStatus = PixStatusHelper.fromPartnerBrcodePreview(bankingPartnerBrcodeStatus)

            // Then
            val expectedResult = PixStatusEnum.PAID
            assertEquals(expectedResult, pixStatus)
        }

        @Test
        fun `Should return CANCELED`() {
            // Given
            val bankingPartnerBrcodeStatus = "canceled"

            // When
            val pixStatus = PixStatusHelper.fromPartnerBrcodePreview(bankingPartnerBrcodeStatus)

            // Then
            val expectedResult = PixStatusEnum.CANCELED
            assertEquals(expectedResult, pixStatus)
        }

        @Test
        fun `Should return UNKNOWN when banking partner brcode status is not mapped`() {
            // Given
            val bankingPartnerBrcodeStatus = "lost"

            // When
            val pixStatus = PixStatusHelper.fromPartnerBrcodePreview(bankingPartnerBrcodeStatus)

            // Then
            val expectedResult = PixStatusEnum.UNKNOWN
            assertEquals(expectedResult, pixStatus)
        }
    }
}