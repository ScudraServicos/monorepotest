package br.com.ume.application.shared.transaction.testBuilders

import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.enums.PixBeneficiaryType
import br.com.ume.application.shared.transaction.enums.TransactionStatusEnum
import br.com.ume.application.shared.transaction.enums.TransactionTypeEnum
import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionDto
import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionOriginDto
import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionBeneficiaryDto
import br.com.ume.application.utils.utcNow
import java.util.*

class TransactionDtoTestBuilder {
    companion object {
        fun build(id: UUID): TransactionDto {
            val utcNow = utcNow()
            return TransactionDto(
                id = id,
                externalId = 1,
                value = 50.0,
                type = TransactionTypeEnum.BRCODE_PAYMENT.toString(),
                txId = "txId",
                partnerExternalId = "partnerExternalId",
                brcode = "brcode",
                status = TransactionStatusEnum.PENDING_CREATION.toString(),
                origin = TransactionOriginDto(
                    id = id,
                    externalId = 1,
                    sourceProductReferenceName = "product",
                    sourceProductReferenceId = "productId",
                    userId = "username",
                    creationTimestamp = utcNow,
                    updateTimestamp = utcNow,
                ),
                beneficiary = TransactionBeneficiaryDto(
                    id = id,
                    externalId = 1,
                    name = "name",
                    legalNature = PixBeneficiaryType.LEGAL_PERSON.toString(),
                    document = "document",
                    pixKey = "pixKey",
                    bankIspbCode = "bankIspbCode",
                    bankAccount = "bankAccount",
                    bankBranch = "bankBranch",
                    creationTimestamp = utcNow,
                    updateTimestamp = utcNow,
                    bankName = "bankName",
                    accountType = "savings"
                ),
                creationTimestamp = utcNow,
                updateTimestamp = utcNow,
            )
        }
    }
}