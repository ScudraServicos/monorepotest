package br.com.ume.application.features.transaction.handleRefund.utils

import br.com.ume.api.controllers.pubSub.transport.updateTransactionStatus.PixPaymentEvent
import br.com.ume.application.features.brcode.shared.brcodeInspected.wrappers.bankingPartner.utils.parsePartnerAmount
import br.com.ume.application.features.transaction.handleRefund.enums.RefundTypeEnum
import br.com.ume.application.shared.transaction.domain.Transaction

fun isPaymentEventDuplicated(transaction: Transaction, event: PixPaymentEvent): Boolean {
    val paymentValue = parsePartnerAmount(event.log.payment.amount)
    return transaction.value == paymentValue
}

fun isPaymentEventOutOfOrder(transaction: Transaction, event: PixPaymentEvent): Boolean {
    val paymentValue = parsePartnerAmount(event.log.payment.amount)
    return transaction.value < paymentValue
}

fun getRefundType(event: PixPaymentEvent, paymentLeftoverCutoffValue: Double): RefundTypeEnum {
    val eventValue = parsePartnerAmount(event.log.payment.amount)

    if (eventValue == 0.0) return RefundTypeEnum.TOTAL
    if (eventValue <= paymentLeftoverCutoffValue) return RefundTypeEnum.TOTAL_WITH_LEFTOVER
    return RefundTypeEnum.PARTIAL
}