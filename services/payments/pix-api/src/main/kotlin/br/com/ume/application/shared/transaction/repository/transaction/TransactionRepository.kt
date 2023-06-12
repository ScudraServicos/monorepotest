package br.com.ume.application.shared.transaction.repository.transaction

import br.com.ume.application.shared.transaction.domain.Transaction
import br.com.ume.application.shared.transaction.enums.TransactionStatusEnum
import br.com.ume.application.shared.transaction.gateway.dtos.CreateTransactionRefundDto
import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionDto
import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionRefundDto
import br.com.ume.application.shared.transaction.repository.transaction.filter.TransactionFilter

interface TransactionRepository {
    fun createTransaction(transactionDto: TransactionDto): String?
    fun createTransactionRefund(createTransactionRefundDto: CreateTransactionRefundDto): TransactionRefundDto?
    fun getTransactions(filter: TransactionFilter): List<TransactionDto>
    fun getTransaction(transactionId: String): TransactionDto?
    fun getTransaction(referenceName: String, referenceId: String): TransactionDto?
    fun getTransactionByExternalId(partnerExternalId: String): TransactionDto?
    fun updateTransactionStatus(transaction: Transaction, status: TransactionStatusEnum): Transaction?
    fun updateTransaction(transactionId: String, status: TransactionStatusEnum, partnerExternalId: String): Boolean
    fun updateTransactionForRefund(transaction: Transaction, value: Double): Transaction?
    fun updateTransactionAsRefunded(transaction: Transaction, value: Double, status: TransactionStatusEnum): Transaction?
}