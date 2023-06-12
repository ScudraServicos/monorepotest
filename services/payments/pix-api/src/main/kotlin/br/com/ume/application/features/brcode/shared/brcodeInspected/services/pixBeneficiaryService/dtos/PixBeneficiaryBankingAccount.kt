package br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.dtos

data class PixBeneficiaryBankingAccount(
    val bankName: String,
    val bankCode: String,
    val branchCode: String,
    val accountNumber: String,
    val accountType: String
) {}