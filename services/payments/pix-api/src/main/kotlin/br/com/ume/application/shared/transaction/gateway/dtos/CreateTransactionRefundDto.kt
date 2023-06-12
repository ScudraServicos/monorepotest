package br.com.ume.application.shared.transaction.gateway.dtos

import br.com.ume.application.features.transaction.handleRefund.enums.RefundTypeEnum
import br.com.ume.application.shared.transaction.domain.Transaction
import java.sql.Timestamp

class CreateTransactionRefundDto(
    val transaction: Transaction,
    val type: RefundTypeEnum,
    val originalValue: Double,
    val previousValue: Double,
    val currentValue: Double,
)