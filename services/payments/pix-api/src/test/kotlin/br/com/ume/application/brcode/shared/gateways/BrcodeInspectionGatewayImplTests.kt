package br.com.ume.application.brcode.shared.gateways

import br.com.ume.application.brcode.shared.testBuilders.BrcodePayloadBuilder
import br.com.ume.application.brcode.shared.testBuilders.BrcodePreviewBuilder
import br.com.ume.application.brcode.shared.testBuilders.DecodedBrcodeBuilder
import br.com.ume.application.brcode.shared.testBuilders.PixBeneficiaryBuilder
import br.com.ume.application.features.brcode.shared.brcodeInspected.domain.BrcodeInspected
import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.PixStatusEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.PixTypeEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.gateways.brcodeInspection.BrcodeInspectionGatewayImpl
import br.com.ume.application.features.brcode.shared.brcodeInspected.gateways.brcodeInspection.BrcodeInspectionGateway
import br.com.ume.application.features.brcode.shared.brcodeInspected.gateways.brcodeInspection.dtos.InspectBrcodeOutput
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeEnrichingService.BrcodeEnrichingService
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeEnrichingService.dtos.GetBrcodePayloadOutput
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.BrcodeService
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.dtos.DecodeBrcodeOutput
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.dtos.GetBrcodePreviewOutput
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.PixBeneficiaryService
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.dtos.GetPixBeneficiaryOutput
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito
import org.mockito.kotlin.never
import java.time.LocalDate
import java.time.LocalDateTime

class BrcodeInspectionGatewayImplTests {
    private lateinit var brcodeEnrichingService: BrcodeEnrichingService
    private lateinit var brcodeServiceMock: BrcodeService
    private lateinit var pixBeneficiaryServiceMock: PixBeneficiaryService
    private lateinit var brcodeInspectionGateway: BrcodeInspectionGateway

    @BeforeEach
    fun setUp() {
        brcodeEnrichingService = Mockito.mock(BrcodeEnrichingService::class.java)
        brcodeServiceMock = Mockito.mock(BrcodeService::class.java)
        pixBeneficiaryServiceMock = Mockito.mock(PixBeneficiaryService::class.java)
        brcodeInspectionGateway = BrcodeInspectionGatewayImpl(brcodeServiceMock, brcodeEnrichingService, pixBeneficiaryServiceMock)
    }

    @Nested
    @DisplayName("inspectBrcode()")
    inner class InspectBrcode {
        @Test
        fun `Should return inspected brcode`() {
            // Given
            val brcode = "00020101021226890014br.gov.bcb.pix2067brcode-h.sandbox.starkinfra.com/v2/3ba1fab80b8f4d8b914c77efaacb7fc55204000053039865802BR5925Ume Desenvolvimento de So6014Belo Horizonte62070503***63044E6E"
            val decodedBrcodeDto = DecodedBrcodeBuilder.buildDynamicBrcode()
            val brCodePreview = BrcodePreviewBuilder.build()
            val brcodePayloadDto = BrcodePayloadBuilder.build()
            val pixBeneficiary = PixBeneficiaryBuilder.buildLegalPerson()

            Mockito.`when`(brcodeServiceMock.decodeBrcode(brcode)).thenReturn(
                DecodeBrcodeOutput(value = decodedBrcodeDto)
            )
            Mockito.`when`(brcodeServiceMock.getBrcodePreview(brcode)).thenReturn(
                GetBrcodePreviewOutput(value = brCodePreview)
            )
            Mockito.`when`(brcodeEnrichingService.getBrcodePayload(decodedBrcodeDto.merchantUrl!!)).thenReturn(
                GetBrcodePayloadOutput(value = brcodePayloadDto)
            )
            Mockito.`when`(pixBeneficiaryServiceMock.getBeneficiary(anyString())).thenReturn(
                GetPixBeneficiaryOutput(value = pixBeneficiary)
            )

            // When
            val inspectedBrcode = brcodeInspectionGateway.inspectBrcode(brcode)

            // Then
            val expectedResult = InspectBrcodeOutput(brcodeInspected = BrcodeInspected(
                pixType = PixTypeEnum.DYNAMIC,
                status = PixStatusEnum.ACTIVE,
                value = 25.0,
                allowAlteration = false,
                txId = null,
                createdAt = LocalDateTime.parse("2023-01-30T14:59:15"),
                presentedAt = LocalDateTime.parse("2023-01-30T14:59:45"),
                expiresAt = LocalDateTime.parse("2023-01-31T14:59:15"),
                dueDate = LocalDate.parse("2023-02-15"),
                withdrawInfo = null,
                changeInfo = null,
                pixBeneficiary = pixBeneficiary
            )
            )
            assertEquals(expectedResult, inspectedBrcode)
            Mockito.verify(pixBeneficiaryServiceMock).getBeneficiary(brcodePayloadDto.chave)
        }

        @Test
        fun `Should return error if decoded brcode has an error`() {
            // Given
            val brcode = "00020126580014br.gov.bcb.pix0136f15ffb8b-0865-47a3-bcc7-fd0415ba1e8f5204000053039865802BR5925Ume Desenvolvimento de So6009Sao Paulo62070503***6304B68A"
            Mockito.`when`(brcodeServiceMock.decodeBrcode(brcode)).thenReturn(
                DecodeBrcodeOutput(error = InspectBrcodeErrorEnum.BRCODE_WITHOUT_URL_AND_KEY)
            )

            // When
            val result = brcodeInspectionGateway.inspectBrcode(brcode)

            // Then
            val expectedResult = InspectBrcodeOutput(error = InspectBrcodeErrorEnum.BRCODE_WITHOUT_URL_AND_KEY)
            assertEquals(expectedResult, result)
        }

        @Test
        fun `Should return error if brcode preview has an error`() {
            // Given
            val brcode = "00020126580014br.gov.bcb.pix0136f15ffb8b-0865-47a3-bcc7-fd0415ba1e8f5204000053039865802BR5925Ume Desenvolvimento de So6009Sao Paulo62070503***6304B68A"
            val decodedBrcodeDto = DecodedBrcodeBuilder.buildStaticBrcode()
            Mockito.`when`(brcodeServiceMock.decodeBrcode(brcode)).thenReturn(DecodeBrcodeOutput(decodedBrcodeDto))
            Mockito.`when`(brcodeServiceMock.getBrcodePreview(brcode)).thenReturn(
                GetBrcodePreviewOutput(error = InspectBrcodeErrorEnum.BANKING_PARTNER_INVALID_QR_CODE)
            )

            // When / Then
            val result = brcodeInspectionGateway.inspectBrcode(brcode)

            // Then
            val expectedResult = InspectBrcodeOutput(error = InspectBrcodeErrorEnum.BANKING_PARTNER_INVALID_QR_CODE)
            assertEquals(expectedResult, result)
        }

        @Test
        fun `Should not get brcode payload if brcode is static`() {
            // Given
            val brcode = "00020126580014br.gov.bcb.pix0136f15ffb8b-0865-47a3-bcc7-fd0415ba1e8f5204000053039865802BR5925Ume Desenvolvimento de So6009Sao Paulo62070503***6304B68A"
            val decodedBrcodeDto = DecodedBrcodeBuilder.buildStaticBrcode()
            val brcodePreviewDto = BrcodePreviewBuilder.build()
            val pixBeneficiary = PixBeneficiaryBuilder.buildLegalPerson()
            Mockito.`when`(brcodeServiceMock.decodeBrcode(brcode)).thenReturn(DecodeBrcodeOutput(decodedBrcodeDto))
            Mockito.`when`(brcodeServiceMock.getBrcodePreview(brcode)).thenReturn(GetBrcodePreviewOutput(brcodePreviewDto))
            Mockito.`when`(pixBeneficiaryServiceMock.getBeneficiary(anyString())).thenReturn(GetPixBeneficiaryOutput(value = pixBeneficiary))

            // When
            val result = brcodeInspectionGateway.inspectBrcode(brcode)

            // Then
            Mockito.verify(brcodeEnrichingService, never()).getBrcodePayload(anyString())
            Mockito.verify(pixBeneficiaryServiceMock).getBeneficiary(decodedBrcodeDto.pixKey!!)
            val expectedResult = InspectBrcodeOutput(
                BrcodeInspected(
                    pixType = PixTypeEnum.STATIC,
                    status = PixStatusEnum.ACTIVE,
                    value = 25.0,
                    allowAlteration = false,
                    txId = null,
                    createdAt = null,
                    presentedAt = null,
                    expiresAt = null,
                    dueDate = null,
                    withdrawInfo = null,
                    changeInfo = null,
                    pixBeneficiary = pixBeneficiary
                )
            )
            assertEquals(expectedResult, result)
        }

        @Test
        fun `Should return error if brcode payload has an error`() {
            // Given
            val brcode = "00020126580014br.gov.bcb.pix0136f15ffb8b-0865-47a3-bcc7-fd0415ba1e8f5204000053039865802BR5925Ume Desenvolvimento de So6009Sao Paulo62070503***6304B68A"
            val decodedBrcodeDto = DecodedBrcodeBuilder.buildDynamicBrcode()
            val brcodePreviewDto = BrcodePreviewBuilder.build()
            Mockito.`when`(brcodeServiceMock.decodeBrcode(brcode)).thenReturn(DecodeBrcodeOutput(decodedBrcodeDto))
            Mockito.`when`(brcodeServiceMock.getBrcodePreview(brcode)).thenReturn(GetBrcodePreviewOutput(brcodePreviewDto))
            Mockito.`when`(brcodeEnrichingService.getBrcodePayload(decodedBrcodeDto.merchantUrl!!)).thenReturn(
                GetBrcodePayloadOutput(error = InspectBrcodeErrorEnum.BRCODE_PAYLOAD_NOT_AVAILABLE)
            )

            // When
            val result = brcodeInspectionGateway.inspectBrcode(brcode)

            // Then
            val expectedResult = InspectBrcodeOutput(error = InspectBrcodeErrorEnum.BRCODE_PAYLOAD_NOT_AVAILABLE)
            assertEquals(expectedResult, result)
        }

        @Test
        fun `Should return error if pix key is not found`() {
            // Given
            val brcode = "00020126580014br.gov.bcb.pix0136f15ffb8b-0865-47a3-bcc7-fd0415ba1e8f5204000053039865802BR5925Ume Desenvolvimento de So6009Sao Paulo62070503***6304B68A"
            val decodedBrcodeDto = DecodedBrcodeBuilder.buildStaticBrcode().copy(pixKey = null)
            val brcodePreviewDto = BrcodePreviewBuilder.build()
            Mockito.`when`(brcodeServiceMock.decodeBrcode(brcode)).thenReturn(DecodeBrcodeOutput(decodedBrcodeDto))
            Mockito.`when`(brcodeServiceMock.getBrcodePreview(brcode)).thenReturn(GetBrcodePreviewOutput(brcodePreviewDto))

            // When
            val result = brcodeInspectionGateway.inspectBrcode(brcode)

            // Then
            val expectedResult = InspectBrcodeOutput(error = InspectBrcodeErrorEnum.PIX_KEY_NOT_FOUND)
            assertEquals(expectedResult, result)
        }

        @Test
        fun `Should return error if pix beneficiary has an error`() {
            // Given
            val brcode = "00020126580014br.gov.bcb.pix0136f15ffb8b-0865-47a3-bcc7-fd0415ba1e8f5204000053039865802BR5925Ume Desenvolvimento de So6009Sao Paulo62070503***6304B68A"
            val decodedBrcodeDto = DecodedBrcodeBuilder.buildStaticBrcode()
            val brcodePreviewDto = BrcodePreviewBuilder.build()
            Mockito.`when`(brcodeServiceMock.decodeBrcode(brcode)).thenReturn(DecodeBrcodeOutput(decodedBrcodeDto))
            Mockito.`when`(brcodeServiceMock.getBrcodePreview(brcode)).thenReturn(GetBrcodePreviewOutput(brcodePreviewDto))
            Mockito.`when`(pixBeneficiaryServiceMock.getBeneficiary(anyString())).thenReturn(
                GetPixBeneficiaryOutput(error = InspectBrcodeErrorEnum.BANKING_PARTNER_INVALID_DICT)
            )

            // When
            val result = brcodeInspectionGateway.inspectBrcode(brcode)

            // Then
            val expectedResult = InspectBrcodeOutput(error = InspectBrcodeErrorEnum.BANKING_PARTNER_INVALID_DICT)
            assertEquals(expectedResult, result)
        }
    }
}