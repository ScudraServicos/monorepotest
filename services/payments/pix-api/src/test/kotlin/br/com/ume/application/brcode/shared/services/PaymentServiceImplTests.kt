package br.com.ume.application.brcode.shared.services

import br.com.ume.application.brcode.shared.testBuilders.BankingPartnerBrcodePaymentBuilder
import br.com.ume.application.features.brcode.payBrcode.useCase.dtos.TransactionOriginDto
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.paymentService.PaymentServiceImpl
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.paymentService.builders.BankingPartnerPaymentDtoBuilder
import br.com.ume.application.features.brcode.shared.brcodeInspected.wrappers.bankingPartner.BankingPartnerWrapper
import org.junit.jupiter.api.*
import com.starkbank.error.InternalServerError as BankingPartnerInternalServerError
import org.junit.jupiter.api.Assertions.*
import com.starkbank.error.InputErrors as BankingPartnerInputErrors
import org.mockito.Mockito
import org.mockito.kotlin.times

class PaymentServiceImplTests {
    private lateinit var bankingPartnerWrapperMock: BankingPartnerWrapper
    private lateinit var paymentService: PaymentServiceImpl

    @BeforeEach
    fun setUp() {
        bankingPartnerWrapperMock = Mockito.mock(BankingPartnerWrapper::class.java)
        paymentService = PaymentServiceImpl(bankingPartnerWrapperMock)
    }

    @Nested
    @DisplayName("payBrcode()")
    inner class PayBrcode {
        @Test
        fun `Should return payment id`() {
            // Given
            val brcode = "123.321"
            val beneficiaryDocument = "123.321.123-12"
            val brcodePayment = BankingPartnerBrcodePaymentBuilder.build()
            val transactionOrigin = TransactionOriginDto(userId = null, sourceProductReferenceId = "123", sourceProductReferenceName = "PAY")
            Mockito.`when`(bankingPartnerWrapperMock.payBrcode(brcode, beneficiaryDocument, listOf("123_PAY"))).thenReturn(brcodePayment)

            // When
            val paymentId = paymentService.payBrcode(brcode, beneficiaryDocument, transactionOrigin)

            // Then
            assertNotNull(paymentId)
            assertEquals(brcodePayment.id, paymentId)
            Mockito.verify(bankingPartnerWrapperMock, times(1)).payBrcode(brcode, beneficiaryDocument, listOf("123_PAY"))
        }

        @Test
        fun `Should return null if banking partner returns invalid`() {
            // Given
            val brcode = "123.321"
            val beneficiaryDocument = "123.321.123-12"
            val transactionOrigin = TransactionOriginDto(userId = null, sourceProductReferenceId = "123", sourceProductReferenceName = "PAY")
            Mockito.`when`(bankingPartnerWrapperMock.payBrcode(brcode, beneficiaryDocument, listOf("123_PAY"))).thenThrow(
                BankingPartnerInputErrors::class.java
            )

            // When
            val paymentId = paymentService.payBrcode(brcode, beneficiaryDocument, transactionOrigin)

            // Then
            assertNull(paymentId)
            Mockito.verify(bankingPartnerWrapperMock, times(1)).payBrcode(brcode, beneficiaryDocument, listOf("123_PAY"))
        }

        @Test
        fun `Should return null if banking partner returns error`() {
            // Given
            val brcode = "123.321"
            val beneficiaryDocument = "123.321.123-12"
            val transactionOrigin = TransactionOriginDto(userId = null, sourceProductReferenceId = "123", sourceProductReferenceName = "PAY")
            Mockito.`when`(bankingPartnerWrapperMock.payBrcode(brcode, beneficiaryDocument, listOf("123_PAY"))).thenThrow(
                BankingPartnerInternalServerError::class.java
            )

            // When
            val paymentId = paymentService.payBrcode(brcode, beneficiaryDocument, transactionOrigin)

            // Then
            assertNull(paymentId)
            Mockito.verify(bankingPartnerWrapperMock, times(1)).payBrcode(brcode, beneficiaryDocument, listOf("123_PAY"))
        }
    }

    @Nested
    @DisplayName("getBrcodePaymentByProduct()")
    inner class GetBrcodePaymentByProduct {
        @Test
        fun `Should return payment`() {
            // Given
            val productId = "123"
            val sourceProduct = "PAY"
            val tag = "123_PAY"
            val bankingPartnerBrcodePayment = BankingPartnerBrcodePaymentBuilder.build()
            Mockito.`when`(bankingPartnerWrapperMock.getBrcodePaymentByTag(tag))
                .thenReturn(bankingPartnerBrcodePayment)

            // When
            val result = paymentService.getBrcodePaymentByProduct(productId, sourceProduct)

            // Then
            val expectedResult = BankingPartnerPaymentDtoBuilder.fromBankingPartnerBrcodePayment(bankingPartnerBrcodePayment)
            assertEquals(expectedResult, result)
            Mockito.verify(bankingPartnerWrapperMock, times(1)).getBrcodePaymentByTag(tag)
        }

        @Test
        fun `Should return payment even with null name`() {
            // Given
            val productId = "123"
            val sourceProduct = "PAY"
            val tag = "123_PAY"
            val bankingPartnerBrcodePayment = BankingPartnerBrcodePaymentBuilder.build()
            bankingPartnerBrcodePayment.name = null
            Mockito.`when`(bankingPartnerWrapperMock.getBrcodePaymentByTag(tag))
                .thenReturn(bankingPartnerBrcodePayment)

            // When
            val result = paymentService.getBrcodePaymentByProduct(productId, sourceProduct)

            // Then
            val expectedResult = BankingPartnerPaymentDtoBuilder.fromBankingPartnerBrcodePayment(bankingPartnerBrcodePayment)
            assertEquals(expectedResult, result)
            Mockito.verify(bankingPartnerWrapperMock, times(1)).getBrcodePaymentByTag(tag)
        }

        @Test
        fun `Should return null if payment is not found`() {
            // Given
            val productId = "123"
            val sourceProduct = "PAY"
            val tag = "123_PAY"
            Mockito.`when`(bankingPartnerWrapperMock.getBrcodePaymentByTag(tag)).thenReturn(null)

            // When
            val result = paymentService.getBrcodePaymentByProduct(productId, sourceProduct)

            // Then
            assertNull(result)
            Mockito.verify(bankingPartnerWrapperMock, times(1)).getBrcodePaymentByTag(tag)
        }

        @Test
        fun `Should throw if partner throws error`() {
            // Given
            val productId = "123"
            val sourceProduct = "PAY"
            val tag = "123_PAY"
            Mockito.`when`(bankingPartnerWrapperMock.getBrcodePaymentByTag(tag)).thenAnswer {
                throw Exception()
            }

            // When / Then
            assertThrows<Exception> {
                paymentService.getBrcodePaymentByProduct(productId, sourceProduct)
            }
            Mockito.verify(bankingPartnerWrapperMock, times(1)).getBrcodePaymentByTag(tag)
        }
    }
}