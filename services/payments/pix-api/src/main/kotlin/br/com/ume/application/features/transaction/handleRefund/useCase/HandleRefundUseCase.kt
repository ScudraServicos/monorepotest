package br.com.ume.application.features.transaction.handleRefund.useCase

import br.com.ume.api.controllers.pubSub.transport.updateTransactionStatus.PixPaymentEvent

interface HandleRefundUseCase {
    fun execute(paymentEvent: PixPaymentEvent)
}