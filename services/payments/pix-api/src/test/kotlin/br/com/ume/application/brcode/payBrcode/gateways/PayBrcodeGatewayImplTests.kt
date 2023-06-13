package br.com.ume.application.brcode.payBrcode.gateways

import br.com.ume.application.brcode.shared.testBuilders.PixBeneficiaryBuilder
import br.com.ume.application.features.brcode.payBrcode.gateways.PayBrcodeGateway
import br.com.ume.application.features.brcode.payBrcode.gateways.PayBrcodeGatewayImpl
import br.com.ume.application.features.brcode.payBrcode.useCase.dtos.TransactionOriginDto
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.paymentService.PaymentService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class PayBrcodeGatewayImplTests {
    private lateinit var paymentServiceMock: PaymentService
    private lateinit var payBrcodeGateway: PayBrcodeGateway

    @BeforeEach
    fun setUp() {
        paymentServiceMock = Mockito.mock(PaymentService::class.java)
        payBrcodeGateway = PayBrcodeGatewayImpl(paymentServiceMock)
    }

    @Nested
    @DisplayName("payBrcode()")
    inner class PayBrcode {
        @Test
        fun `Should return true if payment succeeded`() {
            // Given
            val brcode = "123.321"
            val beneficiary = PixBeneficiaryBuilder.buildLegalPerson()
            val paymentId = "111"
            val transactionOrigin = TransactionOriginDto(userId = null, sourceProductReferenceId = "123", sourceProductReferenceName = "PAY")
            Mockito.`when`(paymentServiceMock.payBrcode(brcode, beneficiary.document, transactionOrigin)).thenReturn(paymentId)

            // When
            val result = payBrcodeGateway.payBrcode(brcode, beneficiary, transactionOrigin)

            // Then
            assertNotNull(result)
        }

        @Test
        fun `Should return false if payment failed`() {
            // Given
            val brcode = "123.321"
            val beneficiary = PixBeneficiaryBuilder.buildLegalPerson()
            val transactionOrigin = TransactionOriginDto(userId = null, sourceProductReferenceId = "123", sourceProductReferenceName = "PAY")
            Mockito.`when`(paymentServiceMock.payBrcode(brcode, beneficiary.document, transactionOrigin)).thenReturn(null)

            // When
            val result = payBrcodeGateway.payBrcode(brcode, beneficiary, transactionOrigin)

            // Then
            assertNull(result)
        }
    }
}