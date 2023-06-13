package br.com.ume.application.features.transaction.shared.dtos

import br.com.ume.application.shared.payment.repository.dtos.PaymentDto

data class PaymentTransactionDto(
    val payment: PaymentDto,
    val transaction: TransactionDto
)