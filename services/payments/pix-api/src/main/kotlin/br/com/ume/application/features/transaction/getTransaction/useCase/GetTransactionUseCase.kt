package br.com.ume.application.features.transaction.getTransaction.useCase

import br.com.ume.application.shared.transaction.domain.Transaction

interface GetTransactionUseCase {
    fun execute(input: GetTransactionUseCaseInput): Transaction
}