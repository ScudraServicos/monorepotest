package br.com.ume.application.features.transaction.shared.builders

import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.transactions.PixApiTransactionResponseDto
import br.com.ume.application.features.transaction.shared.dtos.PaymentTransactionDto
import br.com.ume.application.shared.payment.repository.dtos.PaymentDto

class PaymentTransactionDtoBuilder {
    companion object {
        fun build(pixApiTransaction: PixApiTransactionResponseDto, paymentDto: PaymentDto): PaymentTransactionDto {
            return PaymentTransactionDto(
                payment = paymentDto,
                transaction = TransactionDtoBuilder.build(pixApiTransaction)
            )
        }
    }
}