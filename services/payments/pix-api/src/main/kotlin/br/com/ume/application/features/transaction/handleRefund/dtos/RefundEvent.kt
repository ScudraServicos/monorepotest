package br.com.ume.application.features.transaction.handleRefund.dtos

import br.com.ume.api.controllers.pubSub.transport.updateTransactionStatus.PixPaymentEvent
import br.com.ume.application.shared.transaction.domain.Transaction

data class RefundEvent(
    val transaction: Transaction,
)
