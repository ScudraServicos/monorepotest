package br.com.ume.application.shared.transaction.testBuilders

import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.enums.PixBeneficiaryType
import br.com.ume.application.shared.transaction.domain.Transaction
import br.com.ume.application.shared.transaction.domain.TransactionBeneficiary
import br.com.ume.application.shared.transaction.domain.TransactionOrigin
import br.com.ume.application.shared.transaction.enums.TransactionStatusEnum
import br.com.ume.application.shared.transaction.enums.TransactionTypeEnum
import br.com.ume.application.utils.utcNow

abstract class TransactionBuilder {
    companion object {
        fun build(
            id: String = "4c1ef6f9-3d1a-46e5-8222-b3d674aff767",
            userId: String? = "111",
            value: Double = 20.0,
            status: TransactionStatusEnum = TransactionStatusEnum.PENDING_CREATION
        ): Transaction {
            val utcNow = utcNow()
            return Transaction(
                id = id,
                externalId = 123,
                value = value,
                brcode = "some_brcode",
                partnerExternalId = "123321123",
                txId = "123abc",
                status = status,
                type = TransactionTypeEnum.BRCODE_PAYMENT,
                beneficiary = TransactionBeneficiary(
                    id = "4c1ef6f9-3d1a-46e5-8222-b3d674aff768",
                    externalId = 123,
                    name = "name",
                    legalNature = PixBeneficiaryType.LEGAL_PERSON,
                    document = "document",
                    pixKey = "pixKey",
                    bankIspbCode = "bankIspbCode",
                    bankAccount = "bankAccount",
                    bankBranch = "bankBranch",
                    bankName = "bankName",
                    accountType = "savings",
                    creationTimestamp = utcNow,
                    updateTimestamp = utcNow
                ),
                creationTimestamp = utcNow,
                updateTimestamp = utcNow,
                origin = TransactionOrigin(
                    id = "4c1ef6f9-3d1a-46e5-8222-b3d674aff769",
                    externalId = 123,
                    sourceProductReferenceId = "123",
                    sourceProductReferenceName = "PAY",
                    userId = userId,
                    creationTimestamp = utcNow,
                    updateTimestamp = utcNow
                )
            )
        }
    }
}