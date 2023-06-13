package br.com.ume.application.features.transaction.updateTransactionStatus.useCase.dtos

import br.com.ume.application.shared.transaction.domain.Transaction

data class TransactionFinalizedEvent(
    val transaction: Transaction
)
