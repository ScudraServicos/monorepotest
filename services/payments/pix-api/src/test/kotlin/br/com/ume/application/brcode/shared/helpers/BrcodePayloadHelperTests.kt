package br.com.ume.application.brcode.shared.helpers

import br.com.ume.application.brcode.shared.testBuilders.BrcodePayloadBuilder
import br.com.ume.application.features.brcode.shared.brcodeInspected.helpers.BrcodePayloadHelper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BrcodePayloadHelperTests {
    @Nested
    @DisplayName("decodePayloadToken()")
    inner class DecodePayloadToken {
        @Test
        fun `Should decode payload from valid token`() {
            // Given
            val brcodePayload = BrcodePayloadBuilder.build()
            val token = BrcodePayloadBuilder.buildEncodedPayloadToken(brcodePayload)

            // When
            val result = BrcodePayloadHelper.decodePayloadToken(token)

            // Then
            assertEquals(brcodePayload, result)
        }

        @Test
        fun `Should return null if is given something other than a JWT token`() {
            // Given
            val token = "123987"

            // When
            val result = BrcodePayloadHelper.decodePayloadToken(token)

            // Then
            assertEquals(null, result)
        }

        @Test
        fun `Should return null if an invalid json is decoded`() {
            // Given
            val token = "123.c29uZzogIkJlZXJzIGluIGhlYXZlbiI=.231"

            // When
            val result = BrcodePayloadHelper.decodePayloadToken(token)

            // Then
            assertEquals(null, result)
        }
    }
}