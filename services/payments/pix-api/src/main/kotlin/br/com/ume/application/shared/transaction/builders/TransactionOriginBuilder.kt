package br.com.ume.application.shared.transaction.builders

import br.com.ume.application.shared.transaction.domain.TransactionOrigin
import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionOriginDto

class TransactionOriginBuilder {
    companion object {
        fun buildFromEntity(originEntity: TransactionOriginDto): TransactionOrigin {
            return TransactionOrigin(
                id = originEntity.id.toString(),
                externalId = originEntity.externalId!!,
                sourceProductReferenceName = originEntity.sourceProductReferenceName,
                sourceProductReferenceId = originEntity.sourceProductReferenceId,
                userId = originEntity.userId,
                creationTimestamp = originEntity.creationTimestamp,
                updateTimestamp = originEntity.updateTimestamp
            )
        }
    }
}