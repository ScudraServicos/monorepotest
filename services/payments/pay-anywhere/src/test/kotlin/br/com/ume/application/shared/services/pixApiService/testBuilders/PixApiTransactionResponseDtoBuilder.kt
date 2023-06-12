package br.com.ume.application.shared.services.pixApiService.testBuilders

import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.transactions.PixApiTransactionBeneficiaryDto
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.transactions.PixApiTransactionOriginDto
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.transactions.PixApiTransactionResponseDto
import br.com.ume.application.shared.enums.PixBeneficiaryTypeEnum
import br.com.ume.application.shared.enums.PixPaymentStatusEnum
import br.com.ume.application.shared.utils.utcNow
import java.time.Instant

abstract class PixApiTransactionResponseDtoBuilder {
    companion object {
        fun build(): PixApiTransactionResponseDto {
            val utcNow = utcNow()
            return PixApiTransactionResponseDto(
                value = 20.0,
                brcode = "barcode",
                txId = "txId",
                status = PixPaymentStatusEnum.CREATED,
                creationTimestamp = utcNow,
                updateTimestamp = utcNow,
                beneficiary = PixApiTransactionBeneficiaryDto(
                    name = "name",
                    legalNature = PixBeneficiaryTypeEnum.LEGAL_PERSON,
                    document = "21.744.033/0001-16",
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