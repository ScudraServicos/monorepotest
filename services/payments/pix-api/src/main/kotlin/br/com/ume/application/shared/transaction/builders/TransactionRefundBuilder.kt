package br.com.ume.application.shared.transaction.builders

import br.com.ume.application.features.transaction.handleRefund.enums.RefundTypeEnum
import br.com.ume.application.shared.transaction.domain.TransactionRefund
import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionRefundDto

class TransactionRefundBuilder {
    companion object {
        fun buildFromEntity(transactionRefundDto: TransactionRefundDto): TransactionRefund {
            return TransactionRefund(
                id = transactionRefundDto.id.toString(),
                externalId = transactionRefundDto.externalId,
                type = RefundTypeEnum.valueOf(transactionRefundDto.type),
                originalValue = transactionRefundDto.originalValue,
                previousValue = transactionRefundDto.previousValue,
                currentValue = transactionRefundDto.currentValue,
                creationTimestamp = transactionRefundDto.creationTimestamp,
                updateTimestamp = transactionRefundDto.updateTimestamp
            )
        }
    }
}