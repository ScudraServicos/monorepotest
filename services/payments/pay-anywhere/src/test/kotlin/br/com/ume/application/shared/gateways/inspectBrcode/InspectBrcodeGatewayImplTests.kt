package br.com.ume.application.shared.gateways.inspectBrcode

import br.com.ume.application.features.brcode.inspectBrcode.useCase.dtos.BeneficiaryDto
import br.com.ume.application.features.brcode.inspectBrcode.useCase.dtos.InspectedBrcodeDto
import br.com.ume.application.features.brcode.inspectBrcode.useCase.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.inspectBrcode.gateway.inspectBrcode.InspectBrcodeGateway
import br.com.ume.application.features.brcode.inspectBrcode.gateway.inspectBrcode.InspectBrcodeGatewayImpl
import br.com.ume.application.features.brcode.inspectBrcode.gateway.inspectBrcode.dtos.InspectBrcodeGatewayOutput
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.BrcodeValidationService
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.exceptions.BrcodeValidationException
import br.com.ume.application.features.brcode.shared.services.pixApiService.PixApiService
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.PixApiInspectOutput
import br.com.ume.application.features.brcode.shared.services.pixApiService.enums.PixApiInspectErrorEnum
import br.com.ume.application.shared.testBuilders.PixApiInspectResponseBuilder
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

class InspectBrcodeGatewayImplTests {
    private lateinit var pixApiServiceMock: PixApiService
    private lateinit var brcodeValidationServiceMock: BrcodeValidationService
    private lateinit var inspectBrcodeGateway: InspectBrcodeGateway

    @BeforeEach
    fun setUp() {
        pixApiServiceMock = Mockito.mock(PixApiService::class.java)
        brcodeValidationServiceMock = Mockito.mock(BrcodeValidationService::class.java)
        inspectBrcodeGateway = InspectBrcodeGatewayImpl(pixApiServiceMock, brcodeValidationServiceMock)
    }

    companion object {
        private const val defaultBrCode = "some.brcode"
        private const val defaultUserId = "userId"
    }

    @Nested
    @DisplayName("inspect()")
    inner class Inspect {
        @Test
        fun `Should return inspected brcode`() {
            // Given
            val inspectedBrcode = PixApiInspectResponseBuilder.buildDynamic()
            Mockito.`when`(pixApiServiceMock.inspectBrcode(defaultBrCode)).thenReturn(PixApiInspectOutput(inspectedBrcode))

            // When
            val result = inspectBrcodeGateway.inspect(defaultBrCode, defaultUserId)

            // Then
            val expectedResult = InspectBrcodeGatewayOutput(
                InspectedBrcodeDto(
                    value = inspectedBrcode.value,
                    txId = inspectedBrcode.txId,
                    expiresAt = inspectedBrcode.expiresAt,
                    beneficiary = BeneficiaryDto(
                        name = inspectedBrcode.pixBeneficiary.name,
                        document = inspectedBrcode.pixBeneficiary.document,
                        bankCode = inspectedBrcode.pixBeneficiary.bankingAccount.bankCode,
                        bankName = inspectedBrcode.pixBeneficiary.bankingAccount.bankName,
                        pixKey = inspectedBrcode.pixBeneficiary.pixKey
                    ),
                )
            )
            assertEquals(expectedResult, result)
        }

        @Test
        fun `Should return invalid if pix api returns invalid error`() {
            // Given
            Mockito.`when`(pixApiServiceMock.inspectBrcode(defaultBrCode)).thenReturn(
                PixApiInspectOutput(error = PixApiInspectErrorEnum.BANKING_PARTNER_INVALID_QR_CODE)
            )

            // When
            val result = inspectBrcodeGateway.inspect(defaultBrCode, defaultUserId)

            // Then
            val expectedResult = InspectBrcodeGatewayOutput(
                error = InspectBrcodeErrorEnum.INVALID
            )
            assertEquals(expectedResult, result)
        }

        @Test
        fun `Should return error if pix api returns error`() {
            // Given
            Mockito.`when`(pixApiServiceMock.inspectBrcode(defaultBrCode)).thenReturn(
                PixApiInspectOutput(error = PixApiInspectErrorEnum.BANKING_PARTNER_ERROR)
            )

            // When
            val result = inspectBrcodeGateway.inspect(defaultBrCode, defaultUserId)

            // Then
            val expectedResult = InspectBrcodeGatewayOutput(
                error = InspectBrcodeErrorEnum.ERROR
            )
            assertEquals(expectedResult, result)
        }

        @Test
        fun `Should throw error if validation fails`() {
            // Given
            val inspectedBrcode = PixApiInspectResponseBuilder.buildDynamic()
            Mockito.`when`(pixApiServiceMock.inspectBrcode(defaultBrCode)).thenReturn(PixApiInspectOutput(inspectedBrcode))
            Mockito.`when`(brcodeValidationServiceMock.validate(any(), eq(defaultUserId))).thenAnswer {
                throw BrcodeValidationException(InspectBrcodeErrorEnum.NATURAL_PERSON_BENEFICIARY)
            }

            // When / Then
            val exception = assertThrows<BrcodeValidationException> {
                inspectBrcodeGateway.inspect(defaultBrCode, defaultUserId)
            }
            assertEquals(InspectBrcodeErrorEnum.NATURAL_PERSON_BENEFICIARY.toString(), exception.message)
        }
    }
}