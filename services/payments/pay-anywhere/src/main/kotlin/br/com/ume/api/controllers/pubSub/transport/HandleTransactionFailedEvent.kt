package br.com.ume.api.controllers.pubSub.transport

import br.com.ume.application.features.transaction.shared.dtos.TransactionDto

data class HandleTransactionFailedEvent(
    val transaction: TransactionDto
)