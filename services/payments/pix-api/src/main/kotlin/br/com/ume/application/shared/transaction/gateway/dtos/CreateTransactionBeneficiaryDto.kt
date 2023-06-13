package br.com.ume.application.shared.transaction.gateway.dtos

import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.enums.PixBeneficiaryType

data class CreateTransactionBeneficiaryDto(
    val name: String,
    val legalNature: PixBeneficiaryType,
    val document: String,
    val pixKey: String,
    val bankIspbCode: String,
    val bankBranch: String,
    val bankAccount: String,
    val bankName: String,
    val accountType: String,
)
