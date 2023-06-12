package br.com.ume.application.transaction.useCase

import br.com.ume.api.exceptions.types.InternalErrorException
import br.com.ume.api.exceptions.types.NotFoundException
import br.com.ume.application.features.transaction.shared.gateways.GetPaymentTransactionGateway
import br.com.ume.application.features.transaction.shared.gateways.dtos.GetPaymentTransactionOutput
import br.com.ume.application.features.transaction.shared.gateways.enums.GetPaymentTransactionErrorEnum
import br.com.ume.application.features.transaction.getTransaction.useCase.GetTransactionUseCaseImpl
import br.com.ume.application.features.transaction.getTransaction.useCase.GetTransactionUseCase
import br.com.ume.application.transaction.testBuilders.PaymentTransactionDtoBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito

class GetTransactionUseCaseTests {
    private lateinit var getTransactionGatewayMock: GetPaymentTransactionGateway
    private lateinit var getTransactionUseCase: GetTransactionUseCase

    @BeforeEach
    fun setUp() {
        getTransactionGatewayMock = Mockito.mock(GetPaymentTransactionGateway::class.java)
        getTransactionUseCase = GetTransactionUseCaseImpl(getTransactionGatewayMock)
    }

    @Test
    fun `Should return transaction`() {
        // Given
        val userId = "1"
        val contractId = "1"
        val paymentTransaction = PaymentTransactionDtoBuilder.build()
        Mockito.`when`(getTransactionGatewayMock.getTransactionByContractIdAndUserId(contractId, userId)).thenReturn(
            GetPaymentTransactionOutput(paymentTransaction)
        )

        // When
        val result = getTransactionUseCase.execute(contractId, userId)

        // Then
        assertEquals(paymentTransaction, result)
    }

    @Test
    fun `Should throw NotFound when transaction is not found`() {
        // Given
        val userId = "1"
        val contractId = "1"
        Mockito.`when`(getTransactionGatewayMock.getTransactionByContractIdAndUserId(contractId, userId)).thenReturn(
            GetPaymentTransactionOutput(error = GetPaymentTransactionErrorEnum.TRANSACTION_NOT_FOUND)
        )

        // When
        val exception = assertThrows<NotFoundException> { getTransactionUseCase.execute(contractId, userId) }

        // Then
        val expectedErrorMessage = GetPaymentTransactionErrorEnum.TRANSACTION_NOT_FOUND.toString()
        assertEquals(expectedErrorMessage, exception.message)
    }

    @Test
    fun `Should throw InternalError when an error occurs on getting transaction`() {
        // Given
        val userId = "1"
        val contractId = "1"
        Mockito.`when`(getTransactionGatewayMock.getTransactionByContractIdAndUserId(contractId, userId)).thenReturn(
            GetPaymentTransactionOutput(error = GetPaymentTransactionErrorEnum.UNKNOWN_ERROR)
        )

        // When
        val exception = assertThrows<InternalErrorException> { getTransactionUseCase.execute(contractId, userId) }

        // Then
        val expectedErrorMessage = GetPaymentTransactionErrorEnum.UNKNOWN_ERROR.toString()
        assertEquals(expectedErrorMessage, exception.message)
    }
}