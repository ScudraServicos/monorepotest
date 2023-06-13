package br.com.ume.application.shared.transaction.testBuilders

import br.com.ume.application.shared.transaction.gateway.dtos.CreateBrcodeTransactionDto
import br.com.ume.application.utils.utcNow

class CreateBrcodeTransactionDtoTestBuilder {
    companion object {
        fun build(): CreateBrcodeTransactionDto {
            val utcNow = utcNow()
            return CreateBrcodeTransactionDto(
                value = 50.0,
                txId = "txId",
                partnerExternalId = "partnerExternalId",
                brcode = "brcode",
                beneficiary = CreateTransactionBeneficiaryTestBuilder.build(),
                transactionOrigin = CreateTransactionOriginTestBuilder.build(),
                creationTimestamp = utcNow,
                updateTimestamp = utcNow,
            )
        }
    }
}