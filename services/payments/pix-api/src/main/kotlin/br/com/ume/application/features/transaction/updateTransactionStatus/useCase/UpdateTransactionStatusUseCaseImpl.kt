package br.com.ume.application.features.transaction.updateTransactionStatus.useCase

import br.com.ume.api.configs.FeatureFlagsConfigurations
import br.com.ume.api.configs.TransactionFinalizedTopicConfigurations
import br.com.ume.api.exceptions.types.InternalErrorException
import br.com.ume.application.features.transaction.updateTransactionStatus.useCase.dtos.TransactionFinalizedEvent
import br.com.ume.application.features.transaction.updateTransactionStatus.useCase.enums.UpdateTransactionStatusErrorEnum
import br.com.ume.application.features.transaction.updateTransactionStatus.useCase.utils.isStatusUpdatable
import br.com.ume.application.shared.events.EventProvider
import br.com.ume.application.shared.logging.gcp.JsonLogBuilder
import br.com.ume.application.shared.logging.gcp.LoggerFactory
import br.com.ume.application.shared.transaction.domain.Transaction
import br.com.ume.application.shared.transaction.enums.TransactionStatusEnum
import br.com.ume.application.shared.transaction.gateway.TransactionGateway
import io.micronaut.runtime.http.scope.RequestScope
import java.util.logging.Logger

@RequestScope
class UpdateTransactionStatusUseCaseImpl(
    private val transactionGateway: TransactionGateway,
    private val eventProvider: EventProvider,
    private val transactionFinalizedTopicConfigurations: TransactionFinalizedTopicConfigurations,
    private val featureFlagsConfigurations: FeatureFlagsConfigurations
) : UpdateTransactionStatusUseCase {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(UpdateTransactionStatusUseCaseImpl::class.java.name)
    }

    override fun execute(partnerExternalId: String, status: String) {
        val parsedStatus = TransactionStatusEnum.fromBankingPartnerStatus(status) ?: return
        val transaction = transactionGateway.getTransactionByPartnerExternalId(partnerExternalId)
            ?: throw InternalErrorException(UpdateTransactionStatusErrorEnum.TRANSACTION_NOT_FOUND.toString())

        if (!isStatusUpdatable(transaction.status, parsedStatus)) {
            log.warning(JsonLogBuilder.build(object {
                val message = "TransactionStatusNotUpdatable"
                val transaction = transaction
                val incomingStatus = parsedStatus
            }))
            return
        }

        val transactionUpdated = transactionGateway.updateTransactionStatus(transaction, parsedStatus)
            ?: throw InternalErrorException(UpdateTransactionStatusErrorEnum.UPDATE_STATUS_FAILED.toString())

        if (featureFlagsConfigurations.emitTransactionFinalizedEvent == true && TransactionStatusEnum.isStatusFinal(transactionUpdated.status)) {
            emitTransactionFinalizedEvent(transactionUpdated)
        }
    }

    private fun emitTransactionFinalizedEvent(transaction: Transaction) {
        val eventData = TransactionFinalizedEvent(transaction)
        val attributes = hashMapOf(
            "status" to transaction.status.toString(),
            "sourceProductReferenceName" to transaction.origin.sourceProductReferenceName
        )

        this.eventProvider.publish(
            transactionFinalizedTopicConfigurations.projectId!!,
            transactionFinalizedTopicConfigurations.topicId!!,
            eventData,
            attributes
        )
    }
}