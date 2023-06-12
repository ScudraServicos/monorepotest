package br.com.ume.api.controllers.transactions

import br.com.ume.api.exceptions.base.ApiError
import br.com.ume.api.exceptions.types.InternalErrorException
import br.com.ume.api.exceptions.types.NotFoundException
import br.com.ume.api.transformObjectFromSerializer
import br.com.ume.application.features.transaction.shared.gateways.enums.GetPaymentTransactionErrorEnum
import br.com.ume.application.features.transaction.getTransaction.useCase.GetTransactionUseCaseImpl
import br.com.ume.application.features.transaction.shared.dtos.PaymentTransactionDto
import br.com.ume.application.shared.utils.CustomDeserializer
import br.com.ume.application.transaction.testBuilders.PaymentTransactionDtoBuilder
import io.micronaut.http.HttpStatus
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.restassured.specification.RequestSpecification
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito

@MicronautTest()
class TransactionsControllerTests {
    @Inject
    lateinit var getTransactionUseCase: GetTransactionUseCaseImpl

    @Nested
    @DisplayName("GET /transactions")
    inner class GetTransaction {
        @Test
        fun `Should return transaction`(spec: RequestSpecification) {
            // Given
            val contractId = "1"
            val userId = "1"
            val paymentTransaction = PaymentTransactionDtoBuilder.build()
            Mockito.`when`(getTransactionUseCase.execute(contractId, userId)).thenReturn(paymentTransaction)

            // When
            val response = spec
                .given()
                .header("X-API-KEY", "123")
                .`when`()
                .get("/api/transactions?contractId=$contractId&userId=$userId")

            // Then
            Mockito.verify(getTransactionUseCase, Mockito.times(1)).execute(contractId, userId)
            assertEquals(HttpStatus.OK.code, response.statusCode)
            val responseBody = CustomDeserializer.deserialize(response.body().asString(), PaymentTransactionDto::class.java)
            assertEquals(transformObjectFromSerializer(paymentTransaction), responseBody)
        }

        @Test
        fun `Should return 404 if transaction is not found`(spec: RequestSpecification) {
            // Given
            val contractId = "1"
            val userId = "1"
            Mockito.`when`(getTransactionUseCase.execute(contractId, userId)).thenAnswer {
                throw NotFoundException(GetPaymentTransactionErrorEnum.TRANSACTION_NOT_FOUND.toString())
            }

            // When
            val response = spec
                .given()
                .header("X-API-KEY", "123")
                .`when`()
                .get("/api/transactions?contractId=$contractId&userId=$userId")

            // Then
            Mockito.verify(getTransactionUseCase, Mockito.times(1)).execute(contractId, userId)
            assertEquals(HttpStatus.NOT_FOUND.code, response.statusCode)
            val responseBody = CustomDeserializer.deserialize(response.body().asString(), ApiError::class.java)
            assertEquals(GetPaymentTransactionErrorEnum.TRANSACTION_NOT_FOUND.toString(), responseBody.message)
        }

        @Test
        fun `Should return 500 if transaction has error`(spec: RequestSpecification) {
            // Given
            val contractId = "1"
            val userId = "1"
            Mockito.`when`(getTransactionUseCase.execute(contractId, userId)).thenAnswer {
                throw InternalErrorException(GetPaymentTransactionErrorEnum.UNKNOWN_ERROR.toString())
            }

            // When
            val response = spec
                .given()
                .header("X-API-KEY", "123")
                .`when`()
                .get("/api/transactions?contractId=$contractId&userId=$userId")

            // Then
            Mockito.verify(getTransactionUseCase, Mockito.times(1)).execute(contractId, userId)
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.code, response.statusCode)
            val responseBody = CustomDeserializer.deserialize(response.body().asString(), ApiError::class.java)
            assertEquals(GetPaymentTransactionErrorEnum.UNKNOWN_ERROR.toString(), responseBody.message)
        }

        @Test
        fun `Should return bad request in case no contractId sent`(spec: RequestSpecification) {
            // Given
            val userId = "1"

            // When
            val response = spec
                .given()
                .header("X-API-KEY", "123")
                .`when`()
                .get("/api/transactions?userId=$userId")

            // Then
            assertEquals(HttpStatus.BAD_REQUEST.code, response.statusCode)
        }

        @Test
        fun `Should return bad request in case no userId sent`(spec: RequestSpecification) {
            // Given
            val contractId = "1"

            // When
            val response = spec
                .given()
                .header("X-API-KEY", "123")
                .`when`()
                .get("/api/transactions?contractId=$contractId")

            // Then
            assertEquals(HttpStatus.BAD_REQUEST.code, response.statusCode)
        }

        @Test
        fun `Should return bad request in case contractId is blank`(spec: RequestSpecification) {
            // Given
            val contractId = ""
            val userId = "1"

            // When
            val response = spec
                .given()
                .header("X-API-KEY", "123")
                .`when`()
                .get("/api/transactions?contractId=$contractId&userId=$userId")

            // Then
            assertEquals(HttpStatus.BAD_REQUEST.code, response.statusCode)
        }

        @Test
        fun `Should return bad request in case userId is blank`(spec: RequestSpecification) {
            // Given
            val contractId = "1"
            val userId = ""

            // When
            val response = spec
                .given()
                .header("X-API-KEY", "123")
                .`when`()
                .get("/api/transactions?contractId=$contractId&userId=$userId")

            // Then
            assertEquals(HttpStatus.BAD_REQUEST.code, response.statusCode)
        }
    }

    @MockBean(GetTransactionUseCaseImpl::class)
    fun mockedGetTransactionUseCase() = Mockito.mock(GetTransactionUseCaseImpl::class.java)
}