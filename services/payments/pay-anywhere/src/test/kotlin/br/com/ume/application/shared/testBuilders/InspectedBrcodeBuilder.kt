package br.com.ume.application.shared.testBuilders

import br.com.ume.application.features.brcode.inspectBrcode.useCase.dtos.BeneficiaryDto
import br.com.ume.application.features.brcode.inspectBrcode.useCase.dtos.InspectedBrcodeDto

abstract class InspectedBrcodeBuilder {
    companion object {
        fun buildDynamic(): InspectedBrcodeDto {
            val pixApiInspectedBrcode = PixApiInspectResponseBuilder.buildDynamic()

            return InspectedBrcodeDto(
                value = pixApiInspectedBrcode.value,
                txId = pixApiInspectedBrcode.txId,
                expiresAt = pixApiInspectedBrcode.expiresAt,
                beneficiary = BeneficiaryDto(
                    name = pixApiInspectedBrcode.pixBeneficiary.name,
                    document = pixApiInspectedBrcode.pixBeneficiary.document,
                    bankCode = pixApiInspectedBrcode.pixBeneficiary.bankingAccount.bankCode,
                    bankName = pixApiInspectedBrcode.pixBeneficiary.bankingAccount.bankName,
                    pixKey = pixApiInspectedBrcode.pixBeneficiary.pixKey
                ),
            )
        }

        fun buildStatic(
            value: Double = 25.0,
        ): InspectedBrcodeDto {
            val pixApiInspectedBrcode = PixApiInspectResponseBuilder.buildStatic(value = value)

            return InspectedBrcodeDto(
                value = pixApiInspectedBrcode.value,
                txId = pixApiInspectedBrcode.txId,
                expiresAt = pixApiInspectedBrcode.expiresAt,
                beneficiary = BeneficiaryDto(
                    name = pixApiInspectedBrcode.pixBeneficiary.name,
                    document = pixApiInspectedBrcode.pixBeneficiary.document,
                    bankCode = pixApiInspectedBrcode.pixBeneficiary.bankingAccount.bankCode,
                    bankName = pixApiInspectedBrcode.pixBeneficiary.bankingAccount.bankName,
                    pixKey = pixApiInspectedBrcode.pixBeneficiary.pixKey
                ),
            )
        }
    }
}