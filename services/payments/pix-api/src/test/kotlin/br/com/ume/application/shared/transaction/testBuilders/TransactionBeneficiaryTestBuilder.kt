package br.com.ume.application.shared.transaction.testBuilders

import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.enums.PixBeneficiaryType
import br.com.ume.application.shared.transaction.domain.TransactionBeneficiary
import br.com.ume.application.utils.utcNow

abstract class TransactionBeneficiaryTestBuilder {
    companion object {
        fun build(): TransactionBeneficiary {
            val utcNow = utcNow()
            return TransactionBeneficiary(
                id = "abc123",
                externalId = 123,
                name = "name",
                legalNature = PixBeneficiaryType.NATURAL_PERSON,
                document = "document",
                bankIspbCode = "bankIspbCode",
                bankBranch = "bankBranch",
                bankAccount = "bankAccount",
                bankName = "bankName",
                accountType = "savings",
                pixKey = "pixKey",
                creationTimestamp = utcNow,
                updateTimestamp = utcNow
            )
        }
    }
}