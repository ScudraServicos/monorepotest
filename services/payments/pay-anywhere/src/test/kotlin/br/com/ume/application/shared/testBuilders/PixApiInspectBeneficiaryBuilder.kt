package br.com.ume.application.shared.testBuilders

import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.PixApiInspectBeneficiaryBankingAccountDto
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.PixApiInspectBeneficiaryDto
import br.com.ume.application.shared.enums.PixBeneficiaryTypeEnum

abstract class PixApiInspectBeneficiaryBuilder {
    companion object {
        fun buildNaturalPerson(): PixApiInspectBeneficiaryDto {
            return PixApiInspectBeneficiaryDto(
                name = "Fez",
                document = "***.703.502-**",
                type = PixBeneficiaryTypeEnum.NATURAL_PERSON,
                businessName = null,
                pixKey = "123-321",
                bankingAccount = PixApiInspectBeneficiaryBankingAccountDto(
                    bankName = "Some bank",
                    bankCode = "123",
                    branchCode = "111",
                    accountNumber = "123456",
                    accountType = "salary"
                )
            )
        }
        fun buildLegalPerson(): PixApiInspectBeneficiaryDto {
            return PixApiInspectBeneficiaryDto(
                name = "Chilli's",
                document = "21.744.033/0001-16",
                type = PixBeneficiaryTypeEnum.LEGAL_PERSON,
                businessName = "Chilli's Restaurant",
                pixKey = "321-123",
                bankingAccount = PixApiInspectBeneficiaryBankingAccountDto(
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