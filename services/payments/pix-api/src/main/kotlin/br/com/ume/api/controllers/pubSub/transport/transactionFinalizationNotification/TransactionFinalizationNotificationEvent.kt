package br.com.ume.api.controllers.pubSub.transport.transactionFinalizationNotification

import br.com.ume.application.shared.transaction.domain.Transaction

data class TransactionFinalizationNotificationEvent(
    val transaction: Transaction
)
