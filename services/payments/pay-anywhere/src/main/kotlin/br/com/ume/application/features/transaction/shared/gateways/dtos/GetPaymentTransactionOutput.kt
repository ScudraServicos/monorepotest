package br.com.ume.application.features.transaction.shared.gateways.dtos

import br.com.ume.application.features.transaction.shared.dtos.PaymentTransactionDto
import br.com.ume.application.features.transaction.shared.gateways.enums.GetPaymentTransactionErrorEnum

data class GetPaymentTransactionOutput(
    val value: PaymentTransactionDto? = null,
    val error: GetPaymentTransactionErrorEnum? = null
)
