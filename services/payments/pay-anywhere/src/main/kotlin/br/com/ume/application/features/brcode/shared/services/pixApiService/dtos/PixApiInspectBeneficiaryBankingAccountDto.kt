package br.com.ume.application.features.brcode.shared.services.pixApiService.dtos

data class PixApiInspectBeneficiaryBankingAccountDto(
    val bankName: String,
    val bankCode: String,
    val branchCode: String,
    val accountNumber: String,
    val accountType: String
)
