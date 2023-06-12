package br.com.ume.application.transaction.testBuilders

import br.com.ume.application.features.transaction.shared.dtos.PaymentTransactionDto

class PaymentTransactionDtoBuilder {
    companion object {
        fun build(): PaymentTransactionDto {
            return PaymentTransactionDto(
                payment = PaymentDtoBuilder.build(),
                transaction = TransactionDtoBuilder.build()
            )
        }
    }
}