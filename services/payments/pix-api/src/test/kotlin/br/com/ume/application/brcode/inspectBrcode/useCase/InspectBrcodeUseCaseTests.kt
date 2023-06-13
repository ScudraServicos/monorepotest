package br.com.ume.application.brcode.inspectBrcode.useCase

import br.com.ume.api.exceptions.types.BusinessRuleException
import br.com.ume.api.exceptions.types.InternalErrorException
import br.com.ume.application.brcode.shared.testBuilders.BrcodeInspectedBuilder
import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.gateways.brcodeInspection.BrcodeInspectionGateway
import br.com.ume.application.features.brcode.inspectBrcode.useCase.InspectBrcodeUseCaseImpl
import br.com.ume.application.features.brcode.shared.brcodeInspected.gateways.brcodeInspection.dtos.InspectBrcodeOutput
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito

class InspectBrcodeUseCaseTests {
    private lateinit var inspectBrcodeUseCase: InspectBrcodeUseCaseImpl
    private lateinit var brcodeInspectionGatewayMock: BrcodeInspectionGateway

    @BeforeEach
    fun setUp() {
        brcodeInspectionGatewayMock = Mockito.mock(BrcodeInspectionGateway::class.java)
        inspectBrcodeUseCase = InspectBrcodeUseCaseImpl(brcodeInspectionGatewayMock)
    }

    @Test
    fun `Should return inspected brcode`() {
        // Given
        val brcode = "00020126580014br.gov.bcb.pix0136f15ffb8b-0865-47a3-bcc7-fd0415ba1e8f5204000053039865802BR5925Ume Desenvolvimento de So6009Sao Paulo62070503***6304B68A"
        val inspectedBrcode = BrcodeInspectedBuilder.buildStatic()
        Mockito.`when`(brcodeInspectionGatewayMock.inspectBrcode(brcode)).thenReturn(InspectBrcodeOutput(inspectedBrcode))

        // When
        val result = inspectBrcodeUseCase.execute(brcode)

        // Then
        assertEquals(inspectedBrcode, result)
    }

    @Test
    fun `Should throw business rule exception`() {
        // Given
        val brcode = "00020126580014br.gov.bcb.pix0136f15ffb8b-0865-47a3-bcc7-fd0415ba1e8f5204000053039865802BR5925Ume Desenvolvimento de So6009Sao Paulo62070503***6304B68A"
        Mockito.`when`(brcodeInspectionGatewayMock.inspectBrcode(brcode)).thenReturn(InspectBrcodeOutput(error = InspectBrcodeErrorEnum.BRCODE_WITHOUT_URL_AND_KEY))

        // When / Then
        val exception = assertThrows<BusinessRuleException> {
            inspectBrcodeUseCase.execute(brcode)
        }
        assertEquals(exception.message, InspectBrcodeErrorEnum.BRCODE_WITHOUT_URL_AND_KEY.toString())
    }

    @Test
    fun `Should throw internal error exception`() {
        val brcode = "00020126580014br.gov.bcb.pix0136f15ffb8b-0865-47a3-bcc7-fd0415ba1e8f5204000053039865802BR5925Ume Desenvolvimento de So6009Sao Paulo62070503***6304B68A"
        Mockito.`when`(brcodeInspectionGatewayMock.inspectBrcode(brcode)).thenReturn(InspectBrcodeOutput(error = InspectBrcodeErrorEnum.BRCODE_PAYLOAD_ERROR))

        // When / Then
        val exception = assertThrows<InternalErrorException> {
            inspectBrcodeUseCase.execute(brcode)
        }
        assertEquals(exception.message, InspectBrcodeErrorEnum.BRCODE_PAYLOAD_ERROR.toString())
    }
}