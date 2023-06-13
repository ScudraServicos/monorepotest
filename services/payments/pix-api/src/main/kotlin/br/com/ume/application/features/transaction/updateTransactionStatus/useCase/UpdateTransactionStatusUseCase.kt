package br.com.ume.application.features.transaction.updateTransactionStatus.useCase

interface UpdateTransactionStatusUseCase {
    fun execute(partnerExternalId: String, status: String)
}