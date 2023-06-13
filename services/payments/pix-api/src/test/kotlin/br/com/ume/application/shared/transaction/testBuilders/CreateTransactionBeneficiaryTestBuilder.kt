package br.com.ume.application.shared.transaction.testBuilders

import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.enums.PixBeneficiaryType
import br.com.ume.application.shared.transaction.gateway.dtos.CreateTransactionBeneficiaryDto

abstract class CreateTransactionBeneficiaryTestBuilder {
    companion object {
        fun build(): CreateTransactionBeneficiaryDto {
            return CreateTransactionBeneficiaryDto(
                name = "name",
                legalNature = PixBeneficiaryType.NATURAL_PERSON,
                document = "document",
                bankIspbCode = "bankIspbCode",
                bankBranch = "bankBranch",
                bankAccount = "bankAccount",
                bankName = "bankName",
                accountType = "savings",
                pixKey = "pixKey",
            )
        }
    }
}