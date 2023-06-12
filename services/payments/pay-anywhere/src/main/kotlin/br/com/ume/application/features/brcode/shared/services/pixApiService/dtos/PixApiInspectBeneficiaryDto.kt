package br.com.ume.application.features.brcode.shared.services.pixApiService.dtos

import br.com.ume.application.shared.enums.PixBeneficiaryTypeEnum

data class PixApiInspectBeneficiaryDto(
    val name: String,
    val document: String,
    val type: PixBeneficiaryTypeEnum,
    val businessName: String?,
    val pixKey: String,
    val bankingAccount: PixApiInspectBeneficiaryBankingAccountDto
)
