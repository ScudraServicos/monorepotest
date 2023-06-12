package br.com.ume.application.features.transaction.handleTransactionFailed.useCase

import br.com.ume.application.features.transaction.shared.dtos.TransactionDto

interface HandleTransactionFailedUseCase {
    fun execute(transaction: TransactionDto)
}