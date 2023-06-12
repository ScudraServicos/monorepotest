package br.com.ume.application.shared.transaction.testBuilders

import br.com.ume.application.features.transaction.handleRefund.enums.RefundTypeEnum
import br.com.ume.application.shared.transaction.domain.Transaction
import br.com.ume.application.shared.transaction.domain.TransactionRefund
import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionDto
import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionRefundDto
import br.com.ume.application.utils.utcNow
import java.sql.Timestamp
import java.util.*

fun buildTransactionRefund(
    id: String = "abc123",
    timestamp: Timestamp? = null,
    typeEnum: RefundTypeEnum = RefundTypeEnum.PARTIAL,
    originalValue: Double = 32.0,
    previousValue: Double = 30.0,
    currentValue: Double = 28.0
): TransactionRefund {
    val utcNow = utcNow()

    return TransactionRefund(
        id = id,
        externalId = 123,
        type = typeEnum,
        originalValue = originalValue,
        previousValue = previousValue,
        currentValue = currentValue,
        creationTimestamp = timestamp ?: utcNow,
        updateTimestamp = timestamp ?: utcNow
    )

}

fun buildTransactionRefundDto(transaction: TransactionDto): TransactionRefundDto {
    val utcNow = utcNow()

    return TransactionRefundDto(
        id = UUID.randomUUID(),
        externalId = 123,
        transaction = transaction,
        type = RefundTypeEnum.TOTAL.toString(),
        originalValue = 32.0,
        previousValue = 30.0,
        currentValue = 28.0,
        creationTimestamp = utcNow,
        updateTimestamp = utcNow
    )

}