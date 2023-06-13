package br.com.ume.api.controllers.payments

import br.com.ume.api.controllers.payments.dtos.CreatePaymentRequest
import br.com.ume.application.features.payments.createPayment.useCase.CreatePaymentUseCase
import br.com.ume.libs.logging.gcp.LoggerFactory
import br.com.ume.application.shared.payment.repository.dtos.PaymentDto
import io.micronaut.core.version.annotation.Version
import io.micronaut.http.HttpHeaders
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.MDC
import java.util.logging.Logger

@Controller("/api/payments")
@Validated
@Version("1")
@Tag(name = "Payments")
class PaymentsController(
    private val createPaymentUseCase: CreatePaymentUseCase
) {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(PaymentsController::class.java.name)
    }

    @Post()
    @Operation(summary = "Create payment", description = "Create a payment")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Payment"),
        ApiResponse(responseCode = "404", description = "Contract not found"),
        ApiResponse(responseCode = "500", description = "Internal error"),
    )
    fun createPayment(
        @Header("X-Authorization") token: String,
        @Header("X-Forwarded-For") xForwardedFor: String,
        request: CreatePaymentRequest
    ): PaymentDto {
        MDC.put("useCase", CreatePaymentUseCase::class.java.simpleName)
        MDC.put("proposalId", request.proposalId.trim())
        MDC.put("contractId", request.contractId.trim())
        MDC.put("brCode", request.brCode.trim())
        MDC.put("userId", request.userId.trim())
        log.info("Starting UseCase")

        return this.createPaymentUseCase.execute(
            request.proposalId.trim(),
            request.contractId.trim(),
            request.brCode.trim(),
            request.userId.trim(),
            mapOf("X-Forwarded-For" to xForwardedFor, HttpHeaders.AUTHORIZATION to token)
        )
    }
}