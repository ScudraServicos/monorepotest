package br.com.ume.application.features.transaction.handleRefund.useCase

import br.com.ume.api.configs.RefundConfigurations
import br.com.ume.api.configs.RefundEventConfigurations
import br.com.ume.api.controllers.pubSub.transport.updateTransactionStatus.PixPaymentEvent
import br.com.ume.api.controllers.pubSub.transport.updateTransactionStatus.PixPaymentEventPayment
import br.com.ume.api.exceptions.types.InternalErrorException
import br.com.ume.application.features.brcode.shared.brcodeInspected.wrappers.bankingPartner.utils.parsePartnerAmount
import br.com.ume.application.features.transaction.handleRefund.dtos.RefundEvent
import br.com.ume.application.features.transaction.handleRefund.enums.HandleRefundErrorEnum
import br.com.ume.application.features.transaction.handleRefund.enums.RefundTypeEnum
import br.com.ume.application.features.transaction.handleRefund.utils.getRefundType
import br.com.ume.application.features.transaction.handleRefund.utils.isPaymentEventDuplicated
import br.com.ume.application.features.transaction.handleRefund.utils.isPaymentEventOutOfOrder
import br.com.ume.application.shared.events.EventProvider
import br.com.ume.application.shared.logging.gcp.JsonLogBuilder
import br.com.ume.application.shared.logging.gcp.LoggerFactory
import br.com.ume.application.shared.transaction.domain.Transaction
import br.com.ume.application.shared.transaction.domain.TransactionRefund
import br.com.ume.application.shared.transaction.enums.TransactionStatusEnum
import br.com.ume.application.shared.transaction.gateway.TransactionGateway
import br.com.ume.application.shared.transaction.gateway.dtos.CreateTransactionRefundDto
import io.micronaut.runtime.http.scope.RequestScope
import java.util.logging.Logger
import javax.transaction.Transactional

@RequestScope
class HandleRefundUseCaseImpl(
    private val transactionGateway: TransactionGateway,
    private val refundConfigurations: RefundConfigurations,
    private val eventProvider: EventProvider,
    private val refundEventConfigurations: RefundEventConfigurations
) : HandleRefundUseCase {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(HandleRefundUseCaseImpl::class.java.name)
    }

    @Transactional
    override fun execute(paymentEvent: PixPaymentEvent) {
        val payment = paymentEvent.log.payment
        val transaction = transactionGateway.getTransactionByPartnerExternalId(payment.id)
            ?: throw InternalErrorException(HandleRefundErrorEnum.TRANSACTION_NOT_FOUND.toString())

        if (!shouldHandleEvent(transaction, paymentEvent)) return

        val refundType = getRefundType(paymentEvent, refundConfigurations.paymentLeftoverCutoffValue!!)

        val transactionRefund = createTransactionRefund(transaction, refundType, payment)
        val transactionWithRefund = transaction.copy(refunds = transaction.refunds + transactionRefund)
        val updatedTransaction = updateTransaction(transactionWithRefund, refundType, payment)

        // WARNING: This log is used for sinking and alerting, change it with caution
        log.info(JsonLogBuilder.build(object {
            val message = "HandledTransactionRefund"
            val event = paymentEvent
            val transaction = updatedTransaction
            val transactionRefund = transactionRefund
            val refundType = refundType
            val shouldSinkToDatalake = true
        }))

        emitRefundEvent(updatedTransaction, refundType)
    }

    private fun shouldHandleEvent(transaction: Transaction, paymentEvent: PixPaymentEvent): Boolean {
        if (isPaymentEventDuplicated(transaction, paymentEvent)) {
            log.warning(JsonLogBuilder.build(object {
                val message = "DuplicatedRefundEvent"
                val transaction = transaction
                val event = paymentEvent
            }))
            return false
        }

        if (isPaymentEventOutOfOrder(transaction, paymentEvent)) {
            log.warning(JsonLogBuilder.build(object {
                val message = "OutOfOrderRefundEvent"
                val transaction = transaction
                val event = paymentEvent
            }))
            return false
        }

        return true
    }

    private fun createTransactionRefund(
        transaction: Transaction,
        refundType: RefundTypeEnum,
        payment: PixPaymentEventPayment
    ): TransactionRefund {
        val lastRefund = transaction.refunds.maxByOrNull { it.creationTimestamp }
        val originalValue = lastRefund?.originalValue ?: transaction.value
        val previousValue = lastRefund?.currentValue ?: transaction.value
        val currentValue = parsePartnerAmount(payment.amount)
        val createTransactionRefundDto = CreateTransactionRefundDto(
            transaction, refundType, originalValue, previousValue, currentValue
        )

        val transactionRefund = transactionGateway.createTransactionRefund(createTransactionRefundDto)

        if (transactionRefund == null) {
            log.severe(JsonLogBuilder.build(object {
                val message = "CreateTransactionRefundError"
                val transaction = transaction
                val refundType = refundType
                val eventPayment = payment
            }))
            throw InternalErrorException(HandleRefundErrorEnum.CREATE_TRANSACTION_REFUND_ERROR.toString())
        }

        return transactionRefund
    }

    private fun updateTransaction(
        transaction: Transaction,
        refundType: RefundTypeEnum,
        payment: PixPaymentEventPayment
    ): Transaction {
        val paymentCurrentValue = parsePartnerAmount(payment.amount)

        val updatedTransaction = when(refundType) {
            RefundTypeEnum.PARTIAL -> transactionGateway.updateTransactionForRefund(transaction, paymentCurrentValue)
            RefundTypeEnum.TOTAL, RefundTypeEnum.TOTAL_WITH_LEFTOVER -> transactionGateway.updateTransactionAsRefunded(
                transaction,
                paymentCurrentValue,
                TransactionStatusEnum.REFUNDED
            )
        }

        if (updatedTransaction == null) {
            log.severe(JsonLogBuilder.build(object {
                val message = "UpdateTransactionError"
                val transaction = transaction
                val refundType = refundType
                val eventPayment = payment
            }))
            throw InternalErrorException(HandleRefundErrorEnum.UPDATE_TRANSACTION_ERROR.toString())
        }

        return updatedTransaction
    }

    private fun emitRefundEvent(
        transaction: Transaction,
        refundType: RefundTypeEnum
    ) {
        val eventData = RefundEvent(transaction)
        eventProvider.publish(
            refundEventConfigurations.projectId!!,
            refundEventConfigurations.topicId!!,
            eventData,
            hashMapOf("type" to refundType.toString())
        )
    }
}