package br.com.ume.application.shared.testBuilders.pixApi.transactions

import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.transactions.PixApiTransactionBeneficiaryDto
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.transactions.PixApiTransactionOriginDto
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.transactions.PixApiTransactionResponseDto
import br.com.ume.application.shared.enums.PixBeneficiaryTypeEnum
import br.com.ume.application.shared.enums.PixPaymentStatusEnum
import br.com.ume.application.shared.utils.utcNow
import java.time.Instant

abstract class PixApiGetTransactionResponseBuilder {
    companion object {
        fun build(): PixApiTransactionResponseDto {
            val utcNow = utcNow()
            return PixApiTransactionResponseDto(
                value = 20.0,
                brcode = "brcode",
                txId = "txId",
                status = PixPaymentStatusEnum.CREATED,
                creationTimestamp = utcNow,
                updateTimestamp = utcNow,
                beneficiary = PixApiTransactionBeneficiaryDto(
                    name = "name",
                    legalNature = PixBeneficiaryTypeEnum.LEGAL_PERSON,
                    document = "36.279.336/0001-83",
                    bankBranch = "bankBranch",
                    bankAccount = "bankAccount",
                    bankName = "bankName",
                    accountType = "bankType",
                    pixKey = "pixKey",
                ),
                origin = PixApiTransactionOriginDto(
                    sourceProductReferenceName = "PAY_ANYWHERE",
                    sourceProductReferenceId = "111",
                    userId = "111"
                )
            )
        }
    }
}