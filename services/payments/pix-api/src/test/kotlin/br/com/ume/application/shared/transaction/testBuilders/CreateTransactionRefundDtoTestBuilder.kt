package br.com.ume.application.shared.transaction.testBuilders

import br.com.ume.application.features.transaction.handleRefund.enums.RefundTypeEnum
import br.com.ume.application.shared.transaction.domain.Transaction
import br.com.ume.application.shared.transaction.gateway.dtos.CreateTransactionRefundDto

fun buildCreateTransactionRefundDto(transaction: Transaction, type: RefundTypeEnum = RefundTypeEnum.TOTAL): CreateTransactionRefundDto {
    return CreateTransactionRefundDto(
        transaction = transaction,
        type = type,
        originalValue = 32.0,
        previousValue = 30.0,
        currentValue = 28.0
    )
}