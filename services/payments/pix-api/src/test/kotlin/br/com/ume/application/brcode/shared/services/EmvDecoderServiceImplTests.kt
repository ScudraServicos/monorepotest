package br.com.ume.application.brcode.shared.services

import br.com.ume.application.brcode.shared.testBuilders.DecodedEmvBuilder
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.emvDecoder.EmvDecoderServiceImpl
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class EmvDecoderServiceImplTests {
    private val emvDecoderService = EmvDecoderServiceImpl()

    @Nested
    @DisplayName("decode()")
    inner class DecodeTests {
        @Test
        fun `Should return decoded EMV for dynamic pix`() {
            // Given
            val brcode = "00020101021226890014br.gov.bcb.pix2567brcode-h.sandbox.starkinfra.com/v2/3ba1fab80b8f4d8b914c77efaacb7fc55204000053039865802BR5925Ume Desenvolvimento de So6014Belo Horizonte62070503***63044E6E"

            // When
            val decodedEmvDto = emvDecoderService.decode(brcode)

            // Then
            val expectedMerchantUrl = "brcode-h.sandbox.starkinfra.com/v2/3ba1fab80b8f4d8b914c77efaacb7fc5"
            val expectedResult = DecodedEmvBuilder.build(merchantUrl = expectedMerchantUrl)
            assertEquals(expectedResult, decodedEmvDto)
        }

        @Test
        fun `Should return decoded EMV for static pix`() {
            // Given
            val brcode = "00020126580014br.gov.bcb.pix0136f15ffb8b-0865-47a3-bcc7-fd0415ba1e8f5204000053039865802BR5925Ume Desenvolvimento de So6009Sao Paulo62070503***6304B68A"

            // When
            val decodedEmvDto = emvDecoderService.decode(brcode)

            // Then
            val expectedPixKey = "f15ffb8b-0865-47a3-bcc7-fd0415ba1e8f"
            val expectedResult = DecodedEmvBuilder.build(pixKey = expectedPixKey)
            assertEquals(expectedResult, decodedEmvDto)
        }

        @Test
        fun `Should return null if merchant account info is not found`() {
            // Given
            val brcode = "00020101021227890014br.gov.bcb.pix2567brcode-h.sandbox.starkinfra.com/v2/3ba1fab80b8f4d8b914c77efaacb7fc55204000053039865802BR5925Ume Desenvolvimento de So6014Belo Horizonte62070503***63044E6E"

            // When
            val decodedEmvDto = emvDecoderService.decode(brcode)

            // Then
            val expectedResult = DecodedEmvBuilder.build()
            assertEquals(expectedResult, decodedEmvDto)
        }

        @Test
        fun `Should return null if merchant url is not found`() {
            // Given
            val brcode = "00020101021226890014br.gov.bcb.pix9967brcode-h.sandbox.starkinfra.com/v2/3ba1fab80b8f4d8b914c77efaacb7fc55204000053039865802BR5925Ume Desenvolvimento de So6014Belo Horizonte62070503***63044E6E"

            // When
            val decodedEmvDto = emvDecoderService.decode(brcode)

            // Then
            val expectedResult = DecodedEmvBuilder.build()
            assertEquals(expectedResult, decodedEmvDto)
        }

        @Test
        fun `Should return null if pix key is not found`() {
            // Given
            val brcode = "00020126580014br.gov.bcb.pix0439f15ffb8b-0865-47a3-bcc7-fd0415ba1e8f5204000053039865802BR5925Ume Desenvolvimento de So6009Sao Paulo62070503***6304B68A"

            // When
            val decodedEmvDto = emvDecoderService.decode(brcode)

            // Then
            val expectedResult = DecodedEmvBuilder.build()
            assertEquals(expectedResult, decodedEmvDto)
        }
    }
}