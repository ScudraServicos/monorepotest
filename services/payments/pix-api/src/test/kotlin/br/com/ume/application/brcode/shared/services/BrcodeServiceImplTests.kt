package br.com.ume.application.brcode.shared.services

import br.com.ume.application.brcode.shared.testBuilders.BankingPartnerBrcodePreviewBuilder
import br.com.ume.application.brcode.shared.testBuilders.DecodedBrcodeBuilder
import br.com.ume.application.brcode.shared.testBuilders.DecodedEmvBuilder
import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.PixStatusEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.BrcodeServiceImpl
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.BrcodeService
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.dtos.BrcodePreviewDto
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.dtos.DecodeBrcodeOutput
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.dtos.GetBrcodePreviewOutput
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.emvDecoder.EmvDecoderService
import br.com.ume.application.features.brcode.shared.brcodeInspected.wrappers.bankingPartner.BankingPartnerWrapper
import com.starkbank.error.InputErrors
import com.starkbank.error.InternalServerError as PartnerInternalServerError
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mockito

class BrcodeServiceImplTests {
    private lateinit var bankingPartner: BankingPartnerWrapper
    private lateinit var brcodeService: BrcodeService
    private lateinit var emvDecoderService: EmvDecoderService

    @BeforeEach
    fun setUp() {
        emvDecoderService = Mockito.mock(EmvDecoderService::class.java)
        bankingPartner = Mockito.mock(BankingPartnerWrapper::class.java)
        brcodeService = BrcodeServiceImpl(emvDecoderService, bankingPartner)
    }

    @Nested
    @DisplayName("decodeBrcode()")
    inner class DecodeBrcode {
        @Test
        fun `Should return decoded static brcode`() {
            // Given
            val brcode = "00020126580014br.gov.bcb.pix0136f15ffb8b-0865-47a3-bcc7-fd0415ba1e8f5204000053039865802BR5925Ume Desenvolvimento de So6009Sao Paulo62070503***6304B68A"
            val pixKey = "123"
            val decodedEmvDto = DecodedEmvBuilder.build(pixKey = pixKey)
            Mockito.`when`(emvDecoderService.decode(brcode)).thenReturn(decodedEmvDto)

            // When
            val decodedBrcodeDto = brcodeService.decodeBrcode(brcode)

            // Then
            val expectedResult = DecodeBrcodeOutput(DecodedBrcodeBuilder.buildStaticBrcode(pixKey = pixKey))
            assertEquals(expectedResult, decodedBrcodeDto)
        }

        @Test
        fun `Should return decoded dynamic brcode`() {
            // Given
            val brcode = "00020101021226890014br.gov.bcb.pix2567brcode-h.sandbox.starkinfra.com/v2/3ba1fab80b8f4d8b914c77efaacb7fc55204000053039865802BR5925Ume Desenvolvimento de So6014Belo Horizonte62070503***63044E6E"
            val merchantUrl = "url.com/123"
            val decodedEmvDto = DecodedEmvBuilder.build(merchantUrl = merchantUrl)
            Mockito.`when`(emvDecoderService.decode(brcode)).thenReturn(decodedEmvDto)

            // When
            val decodedBrcodeDto = brcodeService.decodeBrcode(brcode)

            // Then
            val expectedResult = DecodeBrcodeOutput(DecodedBrcodeBuilder.buildDynamicBrcode(merchantUrl = merchantUrl))
            assertEquals(expectedResult, decodedBrcodeDto)
        }

        @Test
        fun `Should return error if has no url nor key`() {
            // Given
            val brcode = "00020101021226890014br.gov.bcb.pix2067brcode-h.sandbox.starkinfra.com/v2/3ba1fab80b8f4d8b914c77efaacb7fc55204000053039865802BR5925Ume Desenvolvimento de So6014Belo Horizonte62070503***63044E6E"
            val decodedEmvDto = DecodedEmvBuilder.build()
            Mockito.`when`(emvDecoderService.decode(brcode)).thenReturn(decodedEmvDto)

            // When
            val decodedBrcodeDto = brcodeService.decodeBrcode(brcode)

            // Then
            val expectedResult = DecodeBrcodeOutput(error = InspectBrcodeErrorEnum.BRCODE_WITHOUT_URL_AND_KEY)
            assertEquals(expectedResult, decodedBrcodeDto)
        }
    }

    @Nested
    @DisplayName("getBrcodePreview()")
    inner class GetBrCodePreview {
        @Test
        fun `Should return brcode preview`() {
            // Given
            val brcode = "00020126580014br.gov.bcb.pix0136f15ffb8b-0865-47a3-bcc7-fd0415ba1e8f5204000053039865802BR5925Ume Desenvolvimento de So6009Sao Paulo62070503***6304B68A"
            val bankingPartnerBrcodePreview = BankingPartnerBrcodePreviewBuilder.buildActiveBrcodePreview()
            Mockito.`when`(bankingPartner.getBrcodePreview(brcode)).thenReturn(bankingPartnerBrcodePreview)

            // When
            val brcodePreview = brcodeService.getBrcodePreview(brcode)

            // Then
            val expectedBrcodePreviewResult = GetBrcodePreviewOutput(value = BrcodePreviewDto(
                status = PixStatusEnum.ACTIVE,
                value = 15.0,
                allowAlteration = bankingPartnerBrcodePreview.allowChange,
                txId = bankingPartnerBrcodePreview.reconciliationId
            ))
            assertEquals(expectedBrcodePreviewResult, brcodePreview)
        }

        @Test
        fun `Should return error if banking partner returns invalid`() {
            // Given
            val brcode = "00020126580014br.gov.bcb.pix0136f15ffb8b-0865-47a3-bcc7-fd0415ba1e8f5204000053039865802BR5925Ume Desenvolvimento de So6009Sao Paulo62070503***6304B68A"
            Mockito.`when`(bankingPartner.getBrcodePreview(brcode)).thenThrow(InputErrors::class.java)

            // When
            val brcodePreview = brcodeService.getBrcodePreview(brcode)

            // Then
            val expectedBrcodePreviewResult = GetBrcodePreviewOutput(error = InspectBrcodeErrorEnum.BANKING_PARTNER_INVALID_QR_CODE)
            assertEquals(expectedBrcodePreviewResult, brcodePreview)
        }

        @Test
        fun `Should return error if banking partner return error`() {
            // Given
            val brcode = "00020126580014br.gov.bcb.pix0136f15ffb8b-0865-47a3-bcc7-fd0415ba1e8f5204000053039865802BR5925Ume Desenvolvimento de So6009Sao Paulo62070503***6304B68A"
            Mockito.`when`(bankingPartner.getBrcodePreview(brcode)).thenThrow(PartnerInternalServerError::class.java)

            // When
            val brcodePreview = brcodeService.getBrcodePreview(brcode)

            // Then
            val expectedBrcodePreviewResult = GetBrcodePreviewOutput(error = InspectBrcodeErrorEnum.BANKING_PARTNER_ERROR)
            assertEquals(expectedBrcodePreviewResult, brcodePreview)
        }
    }
}