package br.com.ume.application.shared.transaction.repository.transaction.filter

data class TransactionFilter(
    val transactionId: String? = null,
    val sourceProductId: String? = null,
    val sourceProductName: String? = null
)