package br.com.ume.api.controllers.pubSub

import br.com.ume.api.controllers.pubSub.transport.createTransaction.CreateTransactionRequest
import br.com.ume.api.controllers.pubSub.transport.updateTransactionStatus.PixPaymentEvent
import br.com.ume.api.eventStream.transport.EventStreamDto
import br.com.ume.application.features.brcode.payBrcode.useCase.PayBrcodeUseCase
import br.com.ume.application.features.brcode.payBrcode.useCase.dtos.TransactionOriginDto
import br.com.ume.application.features.transaction.handleRefund.useCase.HandleRefundUseCase
import br.com.ume.application.features.transaction.notifyTransactionFinalized.useCase.NotifyTransactionFinalizedUseCase
import br.com.ume.application.features.transaction.updateTransactionStatus.useCase.UpdateTransactionStatusUseCase
import br.com.ume.application.shared.logging.gcp.JsonLogBuilder
import br.com.ume.application.shared.transaction.enums.TransactionStatusEnum
import br.com.ume.application.features.transaction.updateTransactionStatus.useCase.dtos.TransactionFinalizedEvent
import br.com.ume.application.shared.logging.gcp.LoggerFactory
import io.micronaut.core.version.annotation.Version
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.MDC
import java.time.Instant
import java.time.ZonedDateTime
import java.util.logging.Logger
import javax.validation.Valid

@Controller("/pubsub")
@Validated
@Version("1")
@Tag(name = "PubSub")
class PubSubController(
    private val updateTransactionStatusUseCase: UpdateTransactionStatusUseCase,
    private val payBrcodeUseCase: PayBrcodeUseCase,
    private val notifyTransactionFinalizedUseCase: NotifyTransactionFinalizedUseCase,
    private val handleRefundUseCase: HandleRefundUseCase
) {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(PubSubController::class.java.name)
        private val eventStatusToLogElapsedTime = setOf(
                TransactionStatusEnum.SUCCESS,
                TransactionStatusEnum.CANCELED,
                TransactionStatusEnum.FAILED,
        )
    }

    @Post("status")
    @Operation(summary = "Update transaction status", description = "Subscriber to event that updates transaction status")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Transaction updated"),
        ApiResponse(responseCode = "500", description = "Unexpected error"),
    )
    fun updateTransactionStatus(@Valid request: EventStreamDto<PixPaymentEvent>) {
        val payment = request.message.data.log.payment

        MDC.put("useCase", UpdateTransactionStatusUseCase::class.java.simpleName)
        MDC.put("partnerExternalId", payment.id)
        MDC.put("status", payment.status)
        log.info("Starting UseCase")

        tryLogPaymentElapsedTime(request.message.data)

        updateTransactionStatusUseCase.execute(payment.id, payment.status)
    }

    @Post("payments")
    @Operation(summary = "Create transaction", description = "Subscriber to event to create transaction")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Transaction created"),
        ApiResponse(responseCode = "500", description = "Unexpected error"),
    )
    fun createTransaction(@Valid request: EventStreamDto<CreateTransactionRequest>) {
        val requestBody = request.message.data
        MDC.put("useCase", PayBrcodeUseCase::class.java.simpleName)
        MDC.put("brcode", requestBody.brCode)
        MDC.put("userId", requestBody.userId)
        log.info("Starting UseCase")

        payBrcodeUseCase.execute(requestBody.brCode,
            TransactionOriginDto(
                requestBody.sourceProductReferenceId,
                requestBody.sourceProductReferenceName,
                userId = requestBody.userId,
            )
        )
    }

    @Post("transaction/finalized")
    @Operation(summary = "Notify transaction finalized", description = "Subscriber to event to notify transaction finalized")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Transaction finalized notified"),
        ApiResponse(responseCode = "500", description = "Unexpected error"),
    )
    fun notifyTransactionFinalized(@Valid request: EventStreamDto<TransactionFinalizedEvent>) {
        val requestBody = request.message.data
        MDC.put("useCase", NotifyTransactionFinalizedUseCase::class.java.simpleName)
        MDC.put("partnerExternalId", requestBody.transaction.partnerExternalId)
        MDC.put("brcode", requestBody.transaction.brcode)
        log.info("Starting UseCase")

        notifyTransactionFinalizedUseCase.execute(requestBody.transaction)
    }

    @Post("transaction/refund")
    @Operation(summary = "Handle transaction refund", description = "Subscriber to event that handles transaction refunds")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Refund handled"),
        ApiResponse(responseCode = "500", description = "Unexpected error"),
    )
    fun handleTransactionRefund(@Valid request: EventStreamDto<PixPaymentEvent>) {
        val event = request.message.data

        MDC.put("useCase", HandleRefundUseCase::class.java.simpleName)
        log.info(JsonLogBuilder.build(object {
            val message = "Starting UseCase"
            val event = event
        }))

        handleRefundUseCase.execute(request.message.data)
    }

    private fun tryLogPaymentElapsedTime(event: PixPaymentEvent) {
        try {
            val paymentLog = event.log
            val payment = paymentLog.payment
            val status = TransactionStatusEnum.fromBankingPartnerStatus(payment.status)
            if (eventStatusToLogElapsedTime.contains(status)) {
                val paymentCreatedOn = ZonedDateTime.parse(payment.created).toInstant().toEpochMilli()
                val elapsedPartnerTimeInMs = ZonedDateTime.parse(paymentLog.created).toInstant().toEpochMilli() - paymentCreatedOn
                val elapsedTimeInMs = Instant.now().toEpochMilli() - paymentCreatedOn
                log.info(JsonLogBuilder.build(object {
                    val message = "Elapsed time in ms to receive payment status update"
                    val paymentCreatedOn = paymentCreatedOn
                    val elapsedPartnerTimeInMs = elapsedPartnerTimeInMs
                    val elapsedTimeInMs = elapsedTimeInMs
                    val diffTimeInMs = elapsedTimeInMs - elapsedPartnerTimeInMs
                    val status = payment.status
                    val service = "pix-api"
                    val shouldSinkToDatalake = true
                    val id = payment.id
                }));
            }
        } catch (error: Exception) {
            log.severe(JsonLogBuilder.build(object {
                val message = "Error logging elapsed time in ms to receive payment status update"
                val event = event
                val error = error
            }));
        }
    }
}