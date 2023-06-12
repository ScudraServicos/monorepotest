package br.com.ume.application.transaction.shared.gateways

import br.com.ume.application.features.brcode.shared.services.pixApiService.PixApiService
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.transactions.PixApiGetTransactionOutput
import br.com.ume.application.features.brcode.shared.services.pixApiService.enums.PixApiTransactionErrorEnum
import br.com.ume.application.features.transaction.shared.builders.PaymentTransactionDtoBuilder
import br.com.ume.application.features.transaction.shared.gateways.GetPaymentTransactionGatewayImpl
import br.com.ume.application.features.transaction.shared.gateways.GetPaymentTransactionGateway
import br.com.ume.application.features.transaction.shared.gateways.dtos.GetPaymentTransactionOutput
import br.com.ume.application.features.transaction.shared.gateways.enums.GetPaymentTransactionErrorEnum
import br.com.ume.application.shared.payment.repository.PaymentRepository
import br.com.ume.application.shared.services.pixApiService.testBuilders.PixApiTransactionResponseDtoBuilder
import br.com.ume.application.transaction.testBuilders.PaymentDtoBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class GetPaymentTransactionGatewayTests {
    private lateinit var pixApiServiceMock: PixApiService
    private lateinit var paymentRepositoryMock: PaymentRepository
    private lateinit var getTransactionGateway: GetPaymentTransactionGateway

    @BeforeEach
    fun setUp() {
        pixApiServiceMock = Mockito.mock(PixApiService::class.java)
        paymentRepositoryMock = Mockito.mock(PaymentRepository::class.java)
        getTransactionGateway = GetPaymentTransactionGatewayImpl(pixApiServiceMock, paymentRepositoryMock)
    }

    @Nested
    @DisplayName("getTransactionByContractId()")
    inner class GetTransaction {
        @Test
        fun `Should return transaction`() {
            // Given
            val userId = "1"
            val contractId = "1"
            val payment = PaymentDtoBuilder.build()
            val pixApiTransaction = PixApiTransactionResponseDtoBuilder.build()
            Mockito.`when`(paymentRepositoryMock.findByContractIdAndUserId(contractId, userId)).thenReturn(payment)
            Mockito.`when`(pixApiServiceMock.getTransaction(payment.id.toString())).thenReturn(
                PixApiGetTransactionOutput(pixApiTransaction)
            )

            // When
            val result = getTransactionGateway.getTransactionByContractIdAndUserId(contractId, userId)

            // Then
            val expectedResult = PaymentTransactionDtoBuilder.build(pixApiTransaction, payment)
            assertEquals(GetPaymentTransactionOutput(expectedResult), result)
            Mockito.verify(paymentRepositoryMock, Mockito.times(1)).findByContractIdAndUserId(contractId, userId)
            Mockito.verify(pixApiServiceMock, Mockito.times(1)).getTransaction(payment.id.toString())
        }

        @Test
        fun `Should return error if pix api returns not found`() {
            // Given
            val userId = "1"
            val contractId = "1"
            val payment = PaymentDtoBuilder.build()
            val pixApiError = PixApiGetTransactionOutput(error = PixApiTransactionErrorEnum.TRANSACTION_NOT_FOUND)
            Mockito.`when`(paymentRepositoryMock.findByContractIdAndUserId(contractId, userId)).thenReturn(payment)
            Mockito.`when`(pixApiServiceMock.getTransaction(payment.id.toString())).thenReturn(pixApiError)

            // When
            val result = getTransactionGateway.getTransactionByContractIdAndUserId(contractId, userId)

            // Then
            val expectedResult = GetPaymentTransactionOutput(error = GetPaymentTransactionErrorEnum.TRANSACTION_NOT_FOUND)
            assertEquals(expectedResult, result)
            Mockito.verify(paymentRepositoryMock, Mockito.times(1)).findByContractIdAndUserId(contractId, userId)
            Mockito.verify(pixApiServiceMock, Mockito.times(1)).getTransaction(payment.id.toString())
        }

        @Test
        fun `Should return error if pix api returns unknown error`() {
            // Given
            val userId = "1"
            val contractId = "1"
            val payment = PaymentDtoBuilder.build()
            val pixApiError = PixApiGetTransactionOutput(error = PixApiTransactionErrorEnum.UNKNOWN_ERROR)
            Mockito.`when`(paymentRepositoryMock.findByContractIdAndUserId(contractId, userId)).thenReturn(payment)
            Mockito.`when`(pixApiServiceMock.getTransaction(payment.id.toString())).thenReturn(pixApiError)

            // When
            val result = getTransactionGateway.getTransactionByContractIdAndUserId(contractId, userId)

            // Then
            val expectedResult = GetPaymentTransactionOutput(error = GetPaymentTransactionErrorEnum.UNKNOWN_ERROR)
            assertEquals(expectedResult, result)
            Mockito.verify(paymentRepositoryMock, Mockito.times(1)).findByContractIdAndUserId(contractId, userId)
            Mockito.verify(pixApiServiceMock, Mockito.times(1)).getTransaction(payment.id.toString())
        }

        @Test
        fun `Should return error if no payment found`() {
            // Given
            val userId = "1"
            val contractId = "1"
            Mockito.`when`(paymentRepositoryMock.findByContractIdAndUserId(contractId, userId)).thenReturn(null)

            // When
            val result = getTransactionGateway.getTransactionByContractIdAndUserId(contractId, userId)

            // Then
            val expectedResult = GetPaymentTransactionOutput(error = GetPaymentTransactionErrorEnum.TRANSACTION_NOT_FOUND)
            assertEquals(expectedResult, result)
            Mockito.verify(paymentRepositoryMock, Mockito.times(1)).findByContractIdAndUserId(contractId, userId)
        }
    }
}