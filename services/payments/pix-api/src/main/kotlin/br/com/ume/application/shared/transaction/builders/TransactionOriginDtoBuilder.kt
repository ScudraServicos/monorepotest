package br.com.ume.application.shared.transaction.builders

import br.com.ume.application.shared.transaction.domain.TransactionOrigin
import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionOriginDto
import java.util.*

class TransactionOriginDtoBuilder {
    companion object {
        fun buildFromTransactionOrigin(transactionOrigin: TransactionOrigin): TransactionOriginDto {
            return TransactionOriginDto(
                id = UUID.fromString(transactionOrigin.id),
                externalId = transactionOrigin.externalId,
                sourceProductReferenceId = transactionOrigin.sourceProductReferenceId,
                sourceProductReferenceName = transactionOrigin.sourceProductReferenceName,
                userId = transactionOrigin.userId,
                creationTimestamp = transactionOrigin.creationTimestamp,
                updateTimestamp = transactionOrigin.updateTimestamp
            )
        }
    }
}