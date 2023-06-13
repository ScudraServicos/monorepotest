package br.com.ume.api.controllers.pubSub

import br.com.ume.api.controllers.pubSub.transport.HandleTransactionFailedEvent
import br.com.ume.api.controllers.pubSub.transport.HandleTotalTransactionRefundEvent
import br.com.ume.api.eventStream.transport.EventStreamDto
import br.com.ume.application.features.transaction.handleTotalRefund.useCase.HandleTotalRefundUseCase
import br.com.ume.application.features.transaction.handleTransactionFailed.useCase.HandleTransactionFailedUseCase
import br.com.ume.libs.logging.gcp.JsonLogBuilder
import br.com.ume.libs.logging.gcp.LoggerFactory
import io.micronaut.core.version.annotation.Version
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.MDC
import java.util.logging.Logger
import javax.validation.Valid

@Controller("/pubsub")
@Validated
@Version("1")
@Tag(name = "PubSub")
class PubSubController(
    private val handleTransactionFailedUseCase: HandleTransactionFailedUseCase,
    private val handleTotalRefundUseCase: HandleTotalRefundUseCase
) {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(PubSubController::class.java.name)
    }

    @Post("handleTransactionFailed")
    @Operation(summary = "Handles failed transaction")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Handled failed transaction"),
        ApiResponse(responseCode = "500", description = "Internal error"),
    )
    fun handleTransactionFailed(@Valid request: EventStreamDto<HandleTransactionFailedEvent>) {
        MDC.put("useCase", HandleTransactionFailedUseCase::class.java.simpleName)
        log.info(JsonLogBuilder.build(object {
            val message = "Starting UseCase"
            val event = request.message.data
        }))

        this.handleTransactionFailedUseCase.execute(request.message.data.transaction)
    }

    @Post("handleTotalTransactionRefund")
    @Operation(summary = "Handles total transaction refund")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Handled total refund"),
        ApiResponse(responseCode = "500", description = "Internal error"),
    )
    fun handleTransactionTotalRefund(@Valid request: EventStreamDto<HandleTotalTransactionRefundEvent>) {
        MDC.put("useCase", HandleTotalRefundUseCase::class.java.simpleName)
        log.info(JsonLogBuilder.build(object {
            val message = "Starting UseCase"
            val event = request.message.data
        }))

        this.handleTotalRefundUseCase.execute(request.message.data.transaction)
    }
}