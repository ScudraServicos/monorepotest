package br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.dtos

import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.enums.PixBeneficiaryType

data class PixBeneficiaryDto(
    val name: String,
    val document: String,
    val type: PixBeneficiaryType,
    val businessName: String?,
    val pixKey: String,
    val bankingAccount: PixBeneficiaryBankingAccount
) {}

