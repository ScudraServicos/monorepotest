package br.com.ume.application.shared.transaction.testBuilders

import br.com.ume.application.shared.transaction.domain.TransactionOrigin
import br.com.ume.application.utils.utcNow

abstract class TransactionOriginTestBuilder {
    companion object {
        fun build(): TransactionOrigin {
            val utcNow = utcNow()

            return TransactionOrigin(
                id = "abc123",
                externalId = 123,
                sourceProductReferenceId = "123",
                sourceProductReferenceName = "PAY",
                userId = "username",
                creationTimestamp = utcNow,
                updateTimestamp = utcNow
            )
        }
    }
}