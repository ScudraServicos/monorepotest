package br.com.ume.api.controllers.pubSub

import br.com.ume.api.controllers.pubSub.transport.HandleTotalTransactionRefundEvent
import br.com.ume.api.controllers.pubSub.transport.HandleTransactionFailedEvent
import br.com.ume.api.exceptions.base.ApiError
import br.com.ume.application.features.transaction.handleTotalRefund.useCase.HandleTotalRefundUseCase
import br.com.ume.application.features.transaction.handleTransactionFailed.useCase.HandleTransactionFailedUseCase
import br.com.ume.application.shared.utils.CustomDeserializer
import br.com.ume.application.shared.utils.CustomSerializer
import br.com.ume.application.transaction.testBuilders.TransactionDtoBuilder
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
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.times
import java.util.*

@MicronautTest
class PubSubControllerTests {
    @Inject
    lateinit var handleTransactionFailedUseCase: HandleTransactionFailedUseCase

    @Inject
    lateinit var handleTotalRefundUseCase: HandleTotalRefundUseCase

    @Nested
    @DisplayName("POST /pubsub/handleTransactionFailed")
    inner class UpdateTransactionUseCase {
        @Test
        fun `Should handle failed transaction`(spec: RequestSpecification) {
            // Given
            val transaction = TransactionDtoBuilder.build()
            val eventBody = serializeEvent(HandleTransactionFailedEvent(transaction))

            doNothing().`when`(handleTransactionFailedUseCase).execute(transaction)

            // When
            val response = spec
                .given()
                .header("Content-Type", "application/json")
                .body(eventBody)
                .`when`()
                .post("/pubsub/handleTransactionFailed")

            // Then
            assertEquals(HttpStatus.OK.code, response.statusCode)
            Mockito.verify(handleTransactionFailedUseCase, times(1)).execute(transaction)
        }

        @Test
        fun `Should return 500 if handling fails`(spec: RequestSpecification) {
            // Given
            val transaction = TransactionDtoBuilder.build()
            val eventBody = serializeEvent(HandleTransactionFailedEvent(transaction))

            Mockito.`when`(handleTransactionFailedUseCase.execute(transaction)).thenAnswer { throw Exception() }

            // When
            val response = spec
                .given()
                .header("Content-Type", "application/json")
                .body(eventBody)
                .`when`()
                .post("/pubsub/handleTransactionFailed")

            // Then
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.code, response.statusCode)
            val responseBody = CustomDeserializer.deserialize(response.body.asString(), ApiError::class.java)
            assertEquals("Internal server error", responseBody.message)
            Mockito.verify(handleTransactionFailedUseCase, times(1)).execute(transaction)
        }
    }

    @Nested
    @DisplayName("POST /pubsub/handleTotalTransactionRefund")
    inner class HandleTotalTransactionRefund {
        @Test
        fun `Should handle total transaction refund`(spec: RequestSpecification) {
            // Given
            val transaction = TransactionDtoBuilder.build()
            val eventBody = serializeEvent(HandleTotalTransactionRefundEvent(transaction))

            doNothing().`when`(handleTotalRefundUseCase).execute(transaction)

            // When
            val response = spec
                .given()
                .header("Content-Type", "application/json")
                .body(eventBody)
                .`when`()
                .post("/pubsub/handleTotalTransactionRefund")

            // Then
            assertEquals(HttpStatus.OK.code, response.statusCode)
            Mockito.verify(handleTotalRefundUseCase, times(1)).execute(transaction)
        }

        @Test
        fun `Should return 500 if handling fails`(spec: RequestSpecification) {
            // Given
            val transaction = TransactionDtoBuilder.build()
            val eventBody = serializeEvent(HandleTotalTransactionRefundEvent(transaction))

            Mockito.`when`(handleTotalRefundUseCase.execute(transaction)).thenAnswer { throw Exception() }

            // When
            val response = spec
                .given()
                .header("Content-Type", "application/json")
                .body(eventBody)
                .`when`()
                .post("/pubsub/handleTotalTransactionRefund")

            // Then
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.code, response.statusCode)
            val responseBody = CustomDeserializer.deserialize(response.body.asString(), ApiError::class.java)
            assertEquals("Internal server error", responseBody.message)
            Mockito.verify(handleTotalRefundUseCase, times(1)).execute(transaction)
        }
    }

    private fun <T> serializeEvent(event: T): String {
        val eventSerialized = CustomSerializer.serialize(event)
        val base64EncodedBody = Base64.getEncoder().encodeToString(eventSerialized.toByteArray())
        return """{
                |"message": {
                |"data": "$base64EncodedBody",
                |"messageId": "111",
                |"publishTime": "2023-03-14T14:45:21.232Z"
                |},
                |"subscription": "123"
                |}
                |""".trimMargin()
    }

    @MockBean(HandleTransactionFailedUseCase::class)
    fun mockedHandleTransactionFailedUseCase() = Mockito.mock(HandleTransactionFailedUseCase::class.java)

    @MockBean(HandleTotalRefundUseCase::class)
    fun mockedHandleTotalRefundUseCase() = Mockito.mock(HandleTotalRefundUseCase::class.java)
}