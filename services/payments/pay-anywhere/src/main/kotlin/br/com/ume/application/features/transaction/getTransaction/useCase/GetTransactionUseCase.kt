package br.com.ume.application.features.transaction.getTransaction.useCase

import br.com.ume.application.features.transaction.shared.dtos.PaymentTransactionDto

interface GetTransactionUseCase {
    fun execute(contractId: String, userId: String): PaymentTransactionDto
}