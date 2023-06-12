package br.com.ume.application.shared.transaction.repository.transaction

import br.com.ume.application.shared.logging.gcp.JsonLogBuilder
import br.com.ume.application.shared.logging.gcp.LoggerFactory
import br.com.ume.application.shared.transaction.builders.TransactionDtoBuilder
import br.com.ume.application.shared.transaction.domain.Transaction
import br.com.ume.application.shared.transaction.enums.TransactionStatusEnum
import br.com.ume.application.shared.transaction.gateway.dtos.CreateTransactionRefundDto
import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionDto
import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionRefundDto
import br.com.ume.application.shared.transaction.repository.transaction.filter.TransactionFilter
import br.com.ume.application.shared.transaction.repository.transaction.filter.transactionsQuerySpecification
import br.com.ume.application.utils.utcNow
import io.micronaut.runtime.http.scope.RequestScope
import java.util.*
import java.util.logging.Logger

@RequestScope
class TransactionRepositoryImpl(
    private val transactionJpaRepository: TransactionJpaRepository,
    private val transactionRefundJpaRepository: TransactionRefundJpaRepository,
) : TransactionRepository {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(TransactionRepositoryImpl::class.java.name)
    }

    override fun createTransaction(transactionDto: TransactionDto): String? {
        return try {
            val transaction = transactionJpaRepository.save(transactionDto)
            return transaction.id.toString()
        } catch (exception: Exception) {
            log.severe(JsonLogBuilder.build(object {
                val message = "Unexpected error while creating transaction"
                val exception = exception
            }))
            null
        }
    }

    override fun createTransactionRefund(createTransactionRefundDto: CreateTransactionRefundDto): TransactionRefundDto? {
        val now = utcNow()
        val transactionDto = TransactionDtoBuilder.buildFromTransaction(createTransactionRefundDto.transaction)
        val transactionRefundDto = TransactionRefundDto(
            transaction = transactionDto,
            type = createTransactionRefundDto.type.toString(),
            originalValue = createTransactionRefundDto.originalValue,
            previousValue = createTransactionRefundDto.previousValue,
            currentValue = createTransactionRefundDto.currentValue,
            creationTimestamp = now,
            updateTimestamp = now
        )

        return try {
            return transactionRefundJpaRepository.save(transactionRefundDto)
        } catch (exception: Exception) {
            log.severe(JsonLogBuilder.build(object {
                val message = "Unexpected error while creating transaction refund"
                val exception = exception
            }))
            null
        }
    }

    override fun getTransactions(filter: TransactionFilter): List<TransactionDto> {
        return transactionJpaRepository.findAll(transactionsQuerySpecification(filter))
    }

    override fun getTransaction(transactionId: String): TransactionDto? {
        val transaction = transactionJpaRepository.findById(UUID.fromString(transactionId))
        return transaction.orElse(null)
    }

    override fun getTransaction(referenceName: String, referenceId: String): TransactionDto? {
        val transaction =
            transactionJpaRepository.findByOriginSourceProductReferenceNameAndOriginSourceProductReferenceId(
                referenceName,
                referenceId
            )
        return transaction.orElse(null)
    }

    override fun getTransactionByExternalId(partnerExternalId: String): TransactionDto? {
        val transaction = transactionJpaRepository.findByPartnerExternalId(partnerExternalId)
        return transaction.orElse(null)
    }

    override fun updateTransactionStatus(transaction: Transaction, status: TransactionStatusEnum): Transaction? {
        return try {
            val updateTimestamp = utcNow()
            val modifiedColumns = transactionJpaRepository.updateStatus(transaction.partnerExternalId!!, status.toString(), updateTimestamp)

            if (modifiedColumns != 1) return null
            return transaction.copy(status = status, updateTimestamp = updateTimestamp)
        } catch (exception: Exception) {
            log.severe(JsonLogBuilder.build(object {
                val message = "Unexpected error updating transaction status"
                val exception = exception
            }))
            null
        }
    }

    override fun updateTransaction(transactionId: String, status: TransactionStatusEnum, partnerExternalId: String): Boolean {
        try {
            // TODO: Encapsulate all invocations of Instant.now() to a function call in order to mock it.
            val modifiedColumns = transactionJpaRepository.update(
                UUID.fromString(transactionId), status.toString(), partnerExternalId, utcNow()
            )
            return modifiedColumns == 1
        } catch (exception: Exception) {
            log.severe(JsonLogBuilder.build(object {
                val message = "Unexpected error updating transaction"
                val exception = exception
            }))
            return false
        }
    }

    override fun updateTransactionForRefund(transaction: Transaction, value: Double): Transaction? {
        return try {
            val updateTimestamp = utcNow()
            val transactionId = UUID.fromString(transaction.id)
            val modifiedColumns = transactionJpaRepository.update(transactionId, value, updateTimestamp)

            if (modifiedColumns != 1) return null
            return transaction.copy(value = value, updateTimestamp = updateTimestamp)
        } catch (exception: Exception) {
            log.severe(JsonLogBuilder.build(object {
                val message = "Unexpected error updating transaction for new refund"
                val exception = exception
            }))
            null
        }
    }

    override fun updateTransactionAsRefunded(
        transaction: Transaction,
        value: Double,
        status: TransactionStatusEnum
    ): Transaction? {
        return try {
            val updateTimestamp = utcNow()
            val transactionId = UUID.fromString(transaction.id)
            val modifiedColumns = transactionJpaRepository.update(transactionId, value, status.toString(), updateTimestamp)

            if (modifiedColumns != 1) return null
            return transaction.copy(
                value = value,
                status = status,
                updateTimestamp = updateTimestamp
            )
        } catch (exception: Exception) {
            log.severe(JsonLogBuilder.build(object {
                val message = "Unexpected error updating transaction as refunded"
                val exception = exception
            }))
            null
        }
    }
}