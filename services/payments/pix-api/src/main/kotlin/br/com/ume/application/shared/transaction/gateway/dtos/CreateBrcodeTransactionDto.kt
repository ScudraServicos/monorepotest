package br.com.ume.application.shared.transaction.gateway.dtos

import br.com.ume.application.shared.transaction.domain.TransactionBeneficiary
import br.com.ume.application.shared.transaction.domain.TransactionOrigin
import java.sql.Timestamp

class CreateBrcodeTransactionDto(
    val value: Double,
    val brcode: String,
    val beneficiary: CreateTransactionBeneficiaryDto,
    val transactionOrigin: CreateTransactionOriginDto,
    val partnerExternalId: String? = null,
    val txId: String?,
    val creationTimestamp: Timestamp,
    val updateTimestamp: Timestamp,
)