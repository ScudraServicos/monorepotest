package br.com.ume.application.features.transaction.shared.builders

import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.transactions.PixApiTransactionResponseDto
import br.com.ume.application.features.transaction.shared.dtos.TransactionDto

abstract class TransactionDtoBuilder {
    companion object {
        fun build(pixApiTransaction: PixApiTransactionResponseDto): TransactionDto {
            return TransactionDto(
                value = pixApiTransaction.value,
                brcode = pixApiTransaction.brcode,
                beneficiary = pixApiTransaction.beneficiary,
                txId = pixApiTransaction.txId,
                status = pixApiTransaction.status,
                creationTimestamp = pixApiTransaction.creationTimestamp,
                updateTimestamp = pixApiTransaction.updateTimestamp,
                origin = pixApiTransaction.origin
            )
        }
    }
}