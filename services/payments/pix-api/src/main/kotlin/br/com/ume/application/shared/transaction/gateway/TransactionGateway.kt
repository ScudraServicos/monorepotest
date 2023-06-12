package br.com.ume.application.shared.transaction.gateway

import br.com.ume.application.features.brcode.shared.brcodeInspected.services.paymentService.dtos.BankingPartnerPaymentDto
import br.com.ume.application.shared.transaction.domain.Transaction
import br.com.ume.application.shared.transaction.domain.TransactionRefund
import br.com.ume.application.shared.transaction.enums.TransactionStatusEnum
import br.com.ume.application.shared.transaction.gateway.dtos.CreateBrcodeTransactionDto
import br.com.ume.application.shared.transaction.gateway.dtos.CreateTransactionRefundDto
import br.com.ume.application.shared.transaction.repository.transaction.filter.TransactionFilter

interface TransactionGateway {
    fun getTransactions(filter: TransactionFilter): List<Transaction>
    fun createBrcodeTransaction(createBrcodeTransactionDto: CreateBrcodeTransactionDto): String?
    fun createTransactionRefund(createTransactionRefundDto: CreateTransactionRefundDto): TransactionRefund?
    fun getTransaction(transactionId: String): Transaction?
    fun getTransactionByPartnerExternalId(partnerExternalId: String): Transaction?
    fun getTransaction(product: String, productId: String): Transaction?
    fun updateTransactionStatus(transaction: Transaction, status: TransactionStatusEnum): Transaction?
    fun updateTransactionForRefund(transaction: Transaction, value: Double): Transaction?
    fun updateTransactionAsRefunded(transaction: Transaction, value: Double, status: TransactionStatusEnum): Transaction?
    fun getBankingPartnerPaymentByProduct(productId: String, sourceProduct: String): BankingPartnerPaymentDto?
    fun updateTransaction(transactionId: String, status: TransactionStatusEnum, partnerExternalId: String): Boolean
}