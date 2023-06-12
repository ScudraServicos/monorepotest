package br.com.ume.application.brcode.shared.testBuilders

import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.dtos.PixBeneficiaryBankingAccount
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.dtos.PixBeneficiaryDto
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.enums.PixBeneficiaryType

abstract class PixBeneficiaryBuilder {
    companion object {
        fun buildNaturalPerson(): PixBeneficiaryDto {
            return PixBeneficiaryDto(
                name = "Fez",
                document = "***.703.502-**",
                type = PixBeneficiaryType.NATURAL_PERSON,
                businessName = null,
                pixKey = "123-321",
                bankingAccount = PixBeneficiaryBankingAccount(
                    bankName = "Some bank",
                    bankCode = "123",
                    branchCode = "111",
                    accountNumber = "123456",
                    accountType = "salary"
                )
            )
        }
        fun buildLegalPerson(): PixBeneficiaryDto {
            return PixBeneficiaryDto(
                name = "Chilli's",
                document = "21.744.033/0001-16",
                type = PixBeneficiaryType.LEGAL_PERSON,
                businessName = "Chilli's Restaurant",
                pixKey = "321-123",
                bankingAccount = PixBeneficiaryBankingAccount(
                    bankName = "Some bank",
                    bankCode = "123",
                    branchCode = "111",
                    accountNumber = "123456",
                    accountType = "checking"
                )
            )
        }
    }
}