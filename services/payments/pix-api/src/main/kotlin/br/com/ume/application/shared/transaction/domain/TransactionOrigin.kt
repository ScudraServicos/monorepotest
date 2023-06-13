package br.com.ume.application.shared.transaction.domain

import java.sql.Timestamp

data class TransactionOrigin(
    val id: String,
    val externalId: Long,
    val sourceProductReferenceName: String,
    val sourceProductReferenceId: String,
    val userId: String?,
    val creationTimestamp: Timestamp,
    val updateTimestamp: Timestamp,
)