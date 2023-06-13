package br.com.ume.application.shared.transaction.testBuilders

import br.com.ume.application.shared.transaction.gateway.dtos.CreateTransactionOriginDto

class CreateTransactionOriginTestBuilder {
    companion object {
        fun build(): CreateTransactionOriginDto {
            return CreateTransactionOriginDto(
                sourceProductReferenceId = "123",
                sourceProductReferenceName = "PAY",
                userId = "username"
            )
        }
    }
}