package br.com.ume.application.features.transaction.handleTotalRefund.useCase

import br.com.ume.application.features.transaction.shared.dtos.TransactionDto
import br.com.ume.application.shared.externalServices.coordinator.CoordinatorService
import br.com.ume.libs.logging.gcp.JsonLogBuilder
import br.com.ume.libs.logging.gcp.LoggerFactory
import br.com.ume.application.shared.payment.gateway.PaymentGateway
import io.micronaut.runtime.http.scope.RequestScope
import java.util.logging.Logger

@RequestScope
class HandleTotalRefundUseCaseImpl(
    private val paymentGateway: PaymentGateway,
    private val coordinatorService: CoordinatorService
) : HandleTotalRefundUseCase {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(HandleTotalRefundUseCaseImpl::class.java.name)
        private const val DEFAULT_REASON = "Reembolso do pagamento PIX"
    }

    override fun execute(transaction: TransactionDto) {
        val paymentId = transaction.origin?.sourceProductReferenceId
        val payment = this.paymentGateway.findPayment(paymentId!!)
        if (payment == null) {
            log.severe(JsonLogBuilder.build(object {
                val message = "PAYMENT_NOT_FOUND"
                val transaction = transaction
            }))
            return
        }

        val contractId = payment.paymentOrigin.contractId
        val contractCanceled = coordinatorService.cancelContract(contractId, DEFAULT_REASON)
        if (contractCanceled) return

        log.severe(JsonLogBuilder.build(object {
            val message = "CONTRACT_CANCEL_ERROR"
            val transaction = transaction
        }))
    }
}