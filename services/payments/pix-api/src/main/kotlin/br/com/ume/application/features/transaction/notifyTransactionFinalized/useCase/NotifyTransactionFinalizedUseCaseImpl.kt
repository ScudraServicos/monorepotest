package br.com.ume.application.features.transaction.notifyTransactionFinalized.useCase

import br.com.ume.api.configs.NotificationsConfigurations
import br.com.ume.application.features.transaction.notifyTransactionFinalized.dtos.TransactionFinalizedNotificationParameters
import br.com.ume.application.features.transaction.notifyTransactionFinalized.dtos.TransactionFinalizedNotification
import br.com.ume.application.shared.events.EventProvider
import br.com.ume.application.shared.logging.gcp.JsonLogBuilder
import br.com.ume.application.shared.logging.gcp.LoggerFactory
import br.com.ume.application.shared.transaction.domain.Transaction
import io.micronaut.runtime.http.scope.RequestScope
import java.text.NumberFormat
import java.util.*

@RequestScope
class NotifyTransactionFinalizedUseCaseImpl(
    private val eventProvider: EventProvider,
    private val notificationsConfigurations: NotificationsConfigurations
) : NotifyTransactionFinalizedUseCase {

    companion object {
        private val log = LoggerFactory.buildStructuredLogger(NotifyTransactionFinalizedUseCaseImpl::class.java.name)
    }

    override fun execute(transaction: Transaction) {
        if (transaction.origin.userId.isNullOrBlank()) {
            log.warning(JsonLogBuilder.build(object {
                val message = "Transaction sent without origin userId"
                val transactionId = transaction.id
            }))
            return
        }

        val eventData = TransactionFinalizedNotification(
            transaction.origin.userId,
            notificationsConfigurations.transactionFinalizedEventName!!,
            TransactionFinalizedNotificationParameters(
                getFormattedValue(transaction.value),
                transaction.status.toString()
            )
        )

        this.eventProvider.publish(
            notificationsConfigurations.projectId!!,
            notificationsConfigurations.topicId!!,
            eventData,
            hashMapOf("status" to transaction.status.toString())
        )
        log.info(JsonLogBuilder.build(object {
            val message = "Notification sent"
            val userId = transaction.origin.userId
            val transactionStatus = transaction.status
        }))
    }

    private fun getFormattedValue(value: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("pt","BR"))
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2
        return formatter.format(value)
    }
}