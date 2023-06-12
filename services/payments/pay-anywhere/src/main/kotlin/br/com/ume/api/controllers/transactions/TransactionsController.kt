package br.com.ume.api.controllers.transactions

import br.com.ume.api.controllers.transactions.transport.getTransaction.GetTransactionRequest
import br.com.ume.application.features.transaction.getTransaction.useCase.GetTransactionUseCaseImpl
import br.com.ume.application.features.transaction.getTransaction.useCase.GetTransactionUseCase
import br.com.ume.application.features.transaction.shared.dtos.PaymentTransactionDto
import br.com.ume.libs.logging.gcp.LoggerFactory
import io.micronaut.core.version.annotation.Version
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.RequestBean
import io.micronaut.validation.Validated
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.MDC
import java.util.logging.Logger
import javax.validation.Valid

@Controller("/api/transactions")
@Validated
@Version("1")
@Tag(name = "Transactions")
class TransactionsController(
    private val getTransactionUseCase: GetTransactionUseCase
) {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(TransactionsController::class.java.name)
    }

    @Get()
    @Operation(summary = "Get transaction", description = "Get single transaction")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Transaction"),
        ApiResponse(responseCode = "404", description = "Transaction not found"),
        ApiResponse(responseCode = "500", description = "Unexpected error")
    )
    fun getTransaction(@Valid @RequestBean getTransactionRequest: GetTransactionRequest): PaymentTransactionDto {
        MDC.put("useCase", GetTransactionUseCaseImpl::class.java.simpleName)
        MDC.put("contractId", getTransactionRequest.contractId)
        log.info("Starting UseCase")

        return getTransactionUseCase.execute(getTransactionRequest.contractId, getTransactionRequest.userId)
    }
}