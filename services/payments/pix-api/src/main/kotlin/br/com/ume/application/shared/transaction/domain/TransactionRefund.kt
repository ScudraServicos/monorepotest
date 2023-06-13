package br.com.ume.application.shared.transaction.domain

import br.com.ume.application.features.transaction.handleRefund.enums.RefundTypeEnum
import java.sql.Timestamp

data class TransactionRefund (
    val id: String,
    val externalId: Long?,
    val type: RefundTypeEnum,
    val originalValue: Double,
    val previousValue: Double,
    val currentValue: Double,
    val creationTimestamp: Timestamp,
    val updateTimestamp: Timestamp
)