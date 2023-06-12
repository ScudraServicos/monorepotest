package br.com.ume.application.shared.transaction.builders

import br.com.ume.application.shared.transaction.domain.Transaction
import br.com.ume.application.shared.transaction.enums.TransactionStatusEnum
import br.com.ume.application.shared.transaction.enums.TransactionTypeEnum
import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionDto

abstract class TransactionBuilder {
    companion object {
        fun buildFromEntity(transactionEntity: TransactionDto): Transaction {
            return Transaction(
                id = transactionEntity.id.toString(),
                externalId = transactionEntity.externalId!!,
                value = transactionEntity.value,
                brcode = transactionEntity.brcode,
                partnerExternalId = transactionEntity.partnerExternalId,
                beneficiary = TransactionBeneficiaryBuilder.buildFromEntity(transactionEntity.beneficiary),
                txId = transactionEntity.txId,
                status = TransactionStatusEnum.valueOf(transactionEntity.status),
                creationTimestamp = transactionEntity.creationTimestamp,
                updateTimestamp = transactionEntity.updateTimestamp,
                origin = TransactionOriginBuilder.buildFromEntity(transactionEntity.origin),
                type = TransactionTypeEnum.valueOf(transactionEntity.type),
                refunds = transactionEntity.refunds.map { TransactionRefundBuilder.buildFromEntity(it) }
            )
        }
    }
}