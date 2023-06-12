package br.com.ume.application.shared.payment.gateway

import br.com.ume.application.shared.payment.repository.PaymentRepository
import br.com.ume.application.transaction.testBuilders.PaymentDtoBuilder
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.mockito.Mockito
import org.mockito.Mockito.times

class PaymentGatewayTests {
    private val paymentRepositoryMock= Mockito.mock(PaymentRepository::class.java)
    private val paymentGateway = PaymentGatewayImpl(paymentRepositoryMock)

    @BeforeEach
    fun clearMocks() {
        Mockito.reset(paymentRepositoryMock)
    }

    @Nested
    @DisplayName("findPayment()")
    inner class FindPayment {
        @Test
        fun `Should return payment`() {
            // Given
            val id = "123"
            val payment = PaymentDtoBuilder.build()
            Mockito.`when`(paymentRepositoryMock.findById(id)).thenReturn(payment)

            // When
            val result = paymentGateway.findPayment(id)

            // Then
            assertEquals(result, payment)
            Mockito.verify(paymentRepositoryMock, times(1)).findById(id)
        }

        @Test
        fun `Should return null if payment is not found`() {
            // Given
            val id = "123"
            Mockito.`when`(paymentRepositoryMock.findById(id)).thenReturn(null)

            // When
            val result = paymentGateway.findPayment(id)

            // Then
            assertNull(result)
            Mockito.verify(paymentRepositoryMock, times(1)).findById(id)
        }

        @Test
        fun `Should throw if find payment fails`() {
            // Given
            val id = "123"
            Mockito.`when`(paymentRepositoryMock.findById(id)).thenAnswer { throw Exception() }

            // When / Then
            assertThrows<Exception> {
                paymentGateway.findPayment(id)
            }
            Mockito.verify(paymentRepositoryMock, times(1)).findById(id)
        }
    }
}