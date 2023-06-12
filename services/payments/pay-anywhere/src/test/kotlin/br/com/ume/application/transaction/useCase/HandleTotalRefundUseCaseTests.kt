package br.com.ume.application.transaction.useCase

import br.com.ume.application.features.transaction.handleTotalRefund.useCase.HandleTotalRefundUseCaseImpl
import br.com.ume.application.shared.externalServices.coordinator.CoordinatorService
import br.com.ume.application.shared.payment.gateway.PaymentGateway
import br.com.ume.application.transaction.testBuilders.PaymentDtoBuilder
import br.com.ume.application.transaction.testBuilders.TransactionDtoBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.kotlin.any

class HandleTotalRefundUseCaseTests {
    private val paymentGatewayMock = Mockito.mock(PaymentGateway::class.java)
    private val coordinatorServiceMock = Mockito.mock(CoordinatorService::class.java)
    private val handleTotalRefundUseCase = HandleTotalRefundUseCaseImpl(paymentGatewayMock, coordinatorServiceMock)

    private val defaultTransaction = TransactionDtoBuilder.build()
    private val defaultPayment = PaymentDtoBuilder.build()
    private val defaultReason = "Reembolso do pagamento PIX"

    @BeforeEach
    fun clearMocks() {
        Mockito.reset(paymentGatewayMock, coordinatorServiceMock)
    }

    @Test
    fun `Should cancel contract`() {
        // Given
        val paymentId = defaultTransaction.origin?.sourceProductReferenceId!!
        val contractId = defaultPayment.paymentOrigin.contractId
        Mockito.`when`(paymentGatewayMock.findPayment(paymentId)).thenReturn(defaultPayment)
        Mockito.`when`(coordinatorServiceMock.cancelContract(contractId, defaultReason)).thenReturn(true)

        // When
        handleTotalRefundUseCase.execute(defaultTransaction)

        // Then
        Mockito.verify(paymentGatewayMock, times(1)).findPayment(paymentId)
        Mockito.verify(coordinatorServiceMock, times(1)).cancelContract(contractId, defaultReason)
    }

    @Test
    fun `Should return if payment is not found`() {
        // Given
        val paymentId = defaultTransaction.origin?.sourceProductReferenceId!!
        Mockito.`when`(paymentGatewayMock.findPayment(paymentId)).thenReturn(null)

        // When
        handleTotalRefundUseCase.execute(defaultTransaction)

        // Then
        Mockito.verify(paymentGatewayMock, times(1)).findPayment(paymentId)
        Mockito.verify(coordinatorServiceMock, times(0)).cancelContract(any(), any())
    }

    @Test
    fun `Should throw if find payment fails`() {
        // Given
        val paymentId = defaultTransaction.origin?.sourceProductReferenceId!!
        Mockito.`when`(paymentGatewayMock.findPayment(paymentId)).thenAnswer { throw Exception() }

        // When
        assertThrows<Exception> { handleTotalRefundUseCase.execute(defaultTransaction) }

        // Then
        Mockito.verify(paymentGatewayMock, times(1)).findPayment(paymentId)
        Mockito.verify(coordinatorServiceMock, times(0)).cancelContract(any(), any())
    }

    @Test
    fun `Should return if contract is not canceled`() {
        // Given
        val paymentId = defaultTransaction.origin?.sourceProductReferenceId!!
        val contractId = defaultPayment.paymentOrigin.contractId
        Mockito.`when`(paymentGatewayMock.findPayment(paymentId)).thenReturn(defaultPayment)
        Mockito.`when`(coordinatorServiceMock.cancelContract(contractId, defaultReason)).thenReturn(false)

        // When
        handleTotalRefundUseCase.execute(defaultTransaction)

        // Then
        Mockito.verify(paymentGatewayMock, times(1)).findPayment(paymentId)
        Mockito.verify(coordinatorServiceMock, times(1)).cancelContract(contractId, defaultReason)
    }


    @Test
    fun `Should throw if cancel contract fails`() {
        // Given
        val paymentId = defaultTransaction.origin?.sourceProductReferenceId!!
        val contractId = defaultPayment.paymentOrigin.contractId
        Mockito.`when`(paymentGatewayMock.findPayment(paymentId)).thenReturn(defaultPayment)
        Mockito.`when`(coordinatorServiceMock.cancelContract(contractId, defaultReason)).thenAnswer { throw Exception() }

        // When
        assertThrows<Exception> { handleTotalRefundUseCase.execute(defaultTransaction) }

        // Then
        Mockito.verify(paymentGatewayMock, times(1)).findPayment(paymentId)
        Mockito.verify(coordinatorServiceMock, times(1)).cancelContract(contractId, defaultReason)
    }
}