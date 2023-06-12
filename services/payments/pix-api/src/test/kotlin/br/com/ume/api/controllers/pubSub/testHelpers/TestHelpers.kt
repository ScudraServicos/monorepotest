package br.com.ume.api.controllers.pubSub.testHelpers

import br.com.ume.api.controllers.pubSub.transport.updateTransactionStatus.PixPaymentEvent
import br.com.ume.api.controllers.pubSub.transport.updateTransactionStatus.PixPaymentEventLog
import br.com.ume.api.controllers.pubSub.transport.updateTransactionStatus.PixPaymentEventPayment
import java.time.LocalDateTime

fun buildBrcodePaymentEvent(amount: Long = 2500): PixPaymentEvent {
    val brcodePayment = PixPaymentEventPayment(
        id = "123321",
        created = LocalDateTime.now().toString(),
        updated = LocalDateTime.now().toString(),
        name = "Tony",
        amount = amount,
        brcode = "00020126580014br.gov.bcb.pix013635719950-ac93-4bab-8ad6-56d7fb63afd252040000530398654040.005802BR5915Stark Bank S.A.6009Sao Paulo62070503***6304AA26",
        description = "Tony Stark's Suit",
        fee = 50,
        scheduled = "2023-03-10T17:53:36.062956+00:00",
        status = "success",
        tags = listOf(),
        taxId = "29.311.808/0001-71",
        transactionIds = listOf(),
        type = "dynamic"
    )
    val log = buildPixPaymentEventLog(brcodePayment)
    return buildPixPaymentEvent(log)
}

fun buildRefundPaymentEvent(amount: Long = 2500): PixPaymentEvent {
    val brcodePayment = PixPaymentEventPayment(
        id = "123321",
        created = LocalDateTime.now().toString(),
        updated = LocalDateTime.now().toString(),
        name = "Tony",
        amount = amount,
        brcode = "00020126580014br.gov.bcb.pix013635719950-ac93-4bab-8ad6-56d7fb63afd252040000530398654040.005802BR5915Stark Bank S.A.6009Sao Paulo62070503***6304AA26",
        description = "Tony Stark's Suit",
        fee = 50,
        scheduled = "2023-03-10T17:53:36.062956+00:00",
        status = "success",
        tags = listOf(),
        taxId = "29.311.808/0001-71",
        transactionIds = listOf(),
        type = "dynamic"
    )
    val log = buildPixPaymentEventLog(brcodePayment, type = "reversed")
    return buildPixPaymentEvent(log)
}

private fun buildPixPaymentEventLog(payment: PixPaymentEventPayment, type: String = "success"): PixPaymentEventLog {
    return PixPaymentEventLog(
        id = "12345",
        type = type,
        created = LocalDateTime.now().toString(),
        payment = payment,
        errors = listOf()
    )
}

private fun buildPixPaymentEvent(eventLog: PixPaymentEventLog): PixPaymentEvent {
    return PixPaymentEvent(
        id = "999",
        log = eventLog,
        created = LocalDateTime.now().toString(),
        subscription = "brcode-payment",
        workspaceId = "123"
    )
}