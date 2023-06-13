package br.com.ume.application.features.transaction.handleTotalRefund.useCase

import br.com.ume.application.features.transaction.shared.dtos.TransactionDto

interface HandleTotalRefundUseCase {
    fun execute(transaction: TransactionDto)
}