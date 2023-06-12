package br.com.ume.api.controllers.pubSub

import br.com.ume.api.controllers.pubSub.testHelpers.buildBrcodePaymentEvent
import br.com.ume.api.controllers.pubSub.testHelpers.buildRefundPaymentEvent
import br.com.ume.api.controllers.pubSub.transport.createTransaction.CreateTransactionRequest
import br.com.ume.api.controllers.pubSub.transport.transactionFinalizationNotification.TransactionFinalizationNotificationEvent
import br.com.ume.api.exceptions.base.ApiError
import br.com.ume.api.exceptions.types.InternalErrorException
import br.com.ume.api.transformObjectFromSerializer
import br.com.ume.application.features.brcode.payBrcode.errors.PayBrcodeErrorEnum
import br.com.ume.application.features.brcode.payBrcode.useCase.PayBrcodeUseCaseImpl
import br.com.ume.application.features.brcode.payBrcode.useCase.dtos.TransactionOriginDto
import br.com.ume.application.features.transaction.handleRefund.enums.HandleRefundErrorEnum
import br.com.ume.application.features.transaction.handleRefund.useCase.HandleRefundUseCaseImpl
import br.com.ume.application.features.transaction.notifyTransactionFinalized.useCase.NotifyTransactionFinalizedUseCaseImpl
import br.com.ume.application.features.transaction.updateTransactionStatus.useCase.UpdateTransactionStatusUseCaseImpl
import br.com.ume.application.features.transaction.updateTransactionStatus.useCase.enums.UpdateTransactionStatusErrorEnum
import br.com.ume.application.shared.transaction.testBuilders.TransactionBuilder
import br.com.ume.application.utils.CustomDeserializer
import br.com.ume.application.utils.CustomSerializer
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
    lateinit var updateTransactionStatusUseCase: UpdateTransactionStatusUseCaseImpl
    @Inject
    lateinit var payBrcodeUseCase: PayBrcodeUseCaseImpl
    @Inject
    lateinit var notifyTransactionFinalizedUseCase: NotifyTransactionFinalizedUseCaseImpl
    @Inject
    lateinit var handleRefundUseCase: HandleRefundUseCaseImpl

    @Nested
    @DisplayName("POST /pubsub/status")
    inner class UpdateTransactionUseCase {
        @Test
        fun `Should update transaction status`(spec: RequestSpecification) {
            // Given
            val event = buildBrcodePaymentEvent()
            val payment = event.log.payment
            val eventBody = serializeEvent(event)

            doNothing().`when`(updateTransactionStatusUseCase).execute(payment.id, payment.status)

            // When
            val response = spec
                .given()
                .header("Content-Type", "application/json")
                .body(eventBody)
                .`when`()
                .post("/pubsub/status")

            // Then
            assertEquals(HttpStatus.OK.code, response.statusCode)
            Mockito.verify(updateTransactionStatusUseCase, times(1)).execute(payment.id, payment.status)
        }

        @Test
        fun `Should return 500 if update fails`(spec: RequestSpecification) {
            // Given
            val event = buildBrcodePaymentEvent()
            val payment = event.log.payment
            val eventBody = serializeEvent(event)

            Mockito.`when`(updateTransactionStatusUseCase.execute(payment.id, payment.status)).thenAnswer {
                throw InternalErrorException(UpdateTransactionStatusErrorEnum.UPDATE_STATUS_FAILED.toString())
            }

            // When
            val response = spec
                .given()
                .header("Content-Type", "application/json")
                .body(eventBody)
                .`when`()
                .post("/pubsub/status")

            // Then
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.code, response.statusCode)
            val responseBody = CustomDeserializer.deserialize(response.body.asString(), ApiError::class.java)
            assertEquals(UpdateTransactionStatusErrorEnum.UPDATE_STATUS_FAILED.toString(), responseBody.message)
            Mockito.verify(updateTransactionStatusUseCase, times(1)).execute(payment.id, payment.status)
        }
    }

    @Nested
    @DisplayName("POST /pubsub/transactions")
    inner class CreateTransaction {
        @Test
        fun `Should create transaction`(spec: RequestSpecification) {
            // Given
            val createTransactionRequest = CreateTransactionRequest(
                brCode = "brcode",
                userId = "userId",
                sourceProductReferenceId = "123",
                sourceProductReferenceName = "PAY"
            )
            val transactionOrigin = TransactionOriginDto(
                sourceProductReferenceId = createTransactionRequest.sourceProductReferenceId,
                sourceProductReferenceName = createTransactionRequest.sourceProductReferenceName,
                userId = createTransactionRequest.userId,
            )
            val transaction = TransactionBuilder.build()
            val eventBody = serializeEvent(createTransactionRequest)

            Mockito.`when`(payBrcodeUseCase.execute(createTransactionRequest.brCode, transactionOrigin = transactionOrigin))
                .thenReturn(transaction)

            // When
            val response = spec
                .given()
                .header("Content-Type", "application/json")
                .body(eventBody)
                .`when`()
                .post("/pubsub/payments")

            // Then
            assertEquals(HttpStatus.OK.code, response.statusCode)
            Mockito.verify(payBrcodeUseCase, times(1)).execute(createTransactionRequest.brCode, transactionOrigin = transactionOrigin)
        }

        @Test
        fun `Should return 500 if transaction creation fails`(spec: RequestSpecification) {
            // Given
            val createTransactionRequest = CreateTransactionRequest(
                brCode = "brcode",
                userId = "userId",
                sourceProductReferenceId = "123",
                sourceProductReferenceName = "PAY"
            )
            val transactionOrigin = TransactionOriginDto(
                sourceProductReferenceId = createTransactionRequest.sourceProductReferenceId,
                sourceProductReferenceName = createTransactionRequest.sourceProductReferenceName,
                userId = createTransactionRequest.userId
            )
            val eventBody = serializeEvent(createTransactionRequest)

            Mockito.`when`(payBrcodeUseCase.execute(createTransactionRequest.brCode, transactionOrigin = transactionOrigin))
                .thenAnswer { throw InternalErrorException(PayBrcodeErrorEnum.TRANSACTION_CREATION_ERROR.toString()) }

            // When
            val response = spec
                .given()
                .header("Content-Type", "application/json")
                .body(eventBody)
                .`when`()
                .post("/pubsub/payments")

            // Then
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.code, response.statusCode)
            val responseBody = CustomDeserializer.deserialize(response.body.asString(), ApiError::class.java)
            assertEquals(PayBrcodeErrorEnum.TRANSACTION_CREATION_ERROR.toString(), responseBody.message)
            Mockito.verify(payBrcodeUseCase, times(1)).execute(createTransactionRequest.brCode, transactionOrigin = transactionOrigin)
        }
    }

    @Nested
    @DisplayName("POST /pubsub/notification")
    inner class NotifyTransactionFinalized {
        @Test
        fun `Should notify transaction finalized`(spec: RequestSpecification) {
            // Given
            val transactionFinalizationNotificationEvent = TransactionFinalizationNotificationEvent(
                transaction = transformObjectFromSerializer(TransactionBuilder.build())
            )
            val eventBody = serializeEvent(transactionFinalizationNotificationEvent)

            doNothing().`when`(notifyTransactionFinalizedUseCase).execute(transactionFinalizationNotificationEvent.transaction)

            // When
            val response = spec
                .given()
                .header("Content-Type", "application/json")
                .body(eventBody)
                .`when`()
                .post("/pubsub/transaction/finalized")

            // Then
            assertEquals(HttpStatus.OK.code, response.statusCode)
            Mockito.verify(notifyTransactionFinalizedUseCase, times(1)).execute(transactionFinalizationNotificationEvent.transaction)
        }

        @Test
        fun `Should return 500 if update fails`(spec: RequestSpecification) {
            // Given
            val transactionFinalizationNotificationEvent = TransactionFinalizationNotificationEvent(
                transaction = transformObjectFromSerializer(TransactionBuilder.build())
            )
            val eventBody = serializeEvent(transactionFinalizationNotificationEvent)

            Mockito.`when`(notifyTransactionFinalizedUseCase.execute(transactionFinalizationNotificationEvent.transaction)).thenAnswer {
                throw InternalErrorException("Exception")
            }

            // When
            val response = spec
                .given()
                .header("Content-Type", "application/json")
                .body(eventBody)
                .`when`()
                .post("/pubsub/transaction/finalized")

            // Then
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.code, response.statusCode)
            Mockito.verify(notifyTransactionFinalizedUseCase, times(1)).execute(transactionFinalizationNotificationEvent.transaction)
        }
    }

    @Nested
    @DisplayName("POST /pubsub/transaction/refund")
    inner class HandleTransactionRefundUseCase {
        private val defaultEvent = buildRefundPaymentEvent()
        private val defaultEventBody = serializeEvent(defaultEvent)

        @Test
        fun `Should handle transaction refund`(spec: RequestSpecification) {
            // Given
            doNothing().`when`(handleRefundUseCase).execute(defaultEvent)

            // When
            val response = spec
                .given()
                .header("Content-Type", "application/json")
                .body(defaultEventBody)
                .`when`()
                .post("/pubsub/transaction/refund")

            // Then
            assertEquals(HttpStatus.OK.code, response.statusCode)
            Mockito.verify(handleRefundUseCase, times(1)).execute(defaultEvent)
        }

        @Test
        fun `Should return 500 if handling fails`(spec: RequestSpecification) {
            // Given
            Mockito.`when`(handleRefundUseCase.execute(defaultEvent)).thenAnswer {
                throw InternalErrorException(HandleRefundErrorEnum.TRANSACTION_NOT_FOUND.toString())
            }

            // When
            val response = spec
                .given()
                .header("Content-Type", "application/json")
                .body(defaultEventBody)
                .`when`()
                .post("/pubsub/transaction/refund")

            // Then
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.code, response.statusCode)
            val responseBody = CustomDeserializer.deserialize(response.body.asString(), ApiError::class.java)
            assertEquals(HandleRefundErrorEnum.TRANSACTION_NOT_FOUND.toString(), responseBody.message)
            Mockito.verify(handleRefundUseCase, times(1)).execute(defaultEvent)
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

    @MockBean(UpdateTransactionStatusUseCaseImpl::class)
    fun mockedUpdateTransactionStatusUseCase() = Mockito.mock(UpdateTransactionStatusUseCaseImpl::class.java)

    @MockBean(PayBrcodeUseCaseImpl::class)
    fun mockedPayBrcodeUseCase() = Mockito.mock(PayBrcodeUseCaseImpl::class.java)

    @MockBean(NotifyTransactionFinalizedUseCaseImpl::class)
    fun mockedNotifyTransactionFinalizedUseCase() = Mockito.mock(NotifyTransactionFinalizedUseCaseImpl::class.java)

    @MockBean(HandleRefundUseCaseImpl::class)
    fun mockedHandleRefundUseCase() = Mockito.mock(HandleRefundUseCaseImpl::class.java)
}