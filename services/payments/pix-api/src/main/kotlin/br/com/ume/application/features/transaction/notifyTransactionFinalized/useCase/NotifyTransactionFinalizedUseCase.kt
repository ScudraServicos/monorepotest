package br.com.ume.application.features.transaction.notifyTransactionFinalized.useCase

import br.com.ume.application.shared.transaction.domain.Transaction

interface NotifyTransactionFinalizedUseCase {
    fun execute(transaction: Transaction)
}