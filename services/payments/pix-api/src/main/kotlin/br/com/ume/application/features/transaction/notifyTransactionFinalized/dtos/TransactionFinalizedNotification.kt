package br.com.ume.application.features.transaction.notifyTransactionFinalized.dtos

data class TransactionFinalizedNotification(
    val borrowerId: String,
    val event: String,
    val parameters: TransactionFinalizedNotificationParameters
)

data class TransactionFinalizedNotificationParameters(
    val value: String,
    val status: String
)