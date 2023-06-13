package br.com.ume.application.shared.transaction.gateway

import br.com.ume.application.features.brcode.shared.brcodeInspected.services.paymentService.PaymentService
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.paymentService.dtos.BankingPartnerPaymentDto
import br.com.ume.application.shared.transaction.builders.TransactionBuilder
import br.com.ume.application.shared.transaction.builders.TransactionDtoBuilder
import br.com.ume.application.shared.transaction.builders.TransactionRefundBuilder
import br.com.ume.application.shared.transaction.domain.Transaction
import br.com.ume.application.shared.transaction.domain.TransactionRefund
import br.com.ume.application.shared.transaction.enums.TransactionStatusEnum
import br.com.ume.application.shared.transaction.gateway.dtos.CreateBrcodeTransactionDto
import br.com.ume.application.shared.transaction.gateway.dtos.CreateTransactionRefundDto
import br.com.ume.application.shared.transaction.repository.transaction.TransactionRepository
import br.com.ume.application.shared.transaction.repository.transaction.filter.TransactionFilter
import io.micronaut.runtime.http.scope.RequestScope

@RequestScope
class TransactionGatewayImpl(
    private val transactionRepository: TransactionRepository,
    private val paymentService: PaymentService
) : TransactionGateway {
    override fun getTransactions(filter: TransactionFilter): List<Transaction> {
        val transactions = transactionRepository.getTransactions(filter)
        return transactions.map { transactionEntity -> TransactionBuilder.buildFromEntity(transactionEntity) }
    }

    override fun createBrcodeTransaction(createBrcodeTransactionDto: CreateBrcodeTransactionDto) : String? {
        return transactionRepository.createTransaction(
            TransactionDtoBuilder.buildBRCodeTransaction(createBrcodeTransactionDto)
        )
    }

    override fun createTransactionRefund(createTransactionRefundDto: CreateTransactionRefundDto): TransactionRefund? {
        val transactionRefundEntity = transactionRepository.createTransactionRefund(createTransactionRefundDto)
            ?: return null

        return TransactionRefundBuilder.buildFromEntity(transactionRefundEntity)
    }

    override fun getTransaction(transactionId: String): Transaction? {
        val transactionEntity = transactionRepository.getTransaction(transactionId) ?: return null

        return TransactionBuilder.buildFromEntity(transactionEntity)
    }

    override fun getTransaction(product: String, productId: String): Transaction? {
        val transactionEntity = transactionRepository.getTransaction(product, productId) ?: return null

        return TransactionBuilder.buildFromEntity(transactionEntity)
    }

    override fun getTransactionByPartnerExternalId(partnerExternalId: String): Transaction? {
        val transactionEntity = transactionRepository.getTransactionByExternalId(partnerExternalId) ?: return null

        return TransactionBuilder.buildFromEntity(transactionEntity)
    }

    override fun updateTransactionStatus(transaction: Transaction, status: TransactionStatusEnum): Transaction? {
        val updateSucceeded = transactionRepository.updateTransactionStatus(transaction, status) ?: return null

        return transaction.copy(
            status = status,
        )
    }

    override fun updateTransactionForRefund(transaction: Transaction, value: Double): Transaction? {
        return transactionRepository.updateTransactionForRefund(transaction, value)
    }

    override fun updateTransactionAsRefunded(
        transaction: Transaction,
        value: Double,
        status: TransactionStatusEnum
    ): Transaction? {
        return transactionRepository.updateTransactionAsRefunded(transaction, value, status)
    }

    override fun getBankingPartnerPaymentByProduct(productId: String, sourceProduct: String): BankingPartnerPaymentDto? {
        return paymentService.getBrcodePaymentByProduct(productId, sourceProduct)
    }

    override fun updateTransaction(transactionId: String, status: TransactionStatusEnum, partnerExternalId: String): Boolean {
        return transactionRepository.updateTransaction(transactionId, status, partnerExternalId)
    }
}