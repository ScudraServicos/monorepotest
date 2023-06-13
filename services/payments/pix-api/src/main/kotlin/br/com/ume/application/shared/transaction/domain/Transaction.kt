package br.com.ume.application.shared.transaction.domain

import br.com.ume.application.shared.transaction.enums.TransactionStatusEnum
import br.com.ume.application.shared.transaction.enums.TransactionTypeEnum
import java.sql.Timestamp

data class Transaction(
    val id: String,
    val externalId: Long,
    val type: TransactionTypeEnum,
    val value: Double,
    val brcode: String?,
    val partnerExternalId: String?,
    val beneficiary: TransactionBeneficiary,
    val txId: String?,
    val status: TransactionStatusEnum,
    val creationTimestamp: Timestamp,
    val updateTimestamp: Timestamp,
    val origin: TransactionOrigin,
    val refunds: List<TransactionRefund> = emptyList()
)