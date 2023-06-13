package br.com.ume.api.controllers.pubSub.transport.updateTransactionStatus

data class PixPaymentEvent(
    val id: String,
    val created: String,
    val subscription: String,
    val workspaceId: String,
    val log: PixPaymentEventLog,
)

data class PixPaymentEventLog(
    val id: String,
    val created: String,
    val type: String,
    val errors: List<String>,
    val payment: PixPaymentEventPayment
)

data class PixPaymentEventPayment(
    val id: String,
    val created: String,
    val updated: String,
    val name: String?,
    val amount: Long,
    val brcode: String,
    val description: String,
    val fee: Long,
    val scheduled: String,
    val status: String,
    val tags: List<String>,
    val taxId: String,
    val transactionIds: List<String>,
    val type: String,
)
