package br.com.ume.application.features.brcode.shared.brcodeInspected.wrappers.bankingPartner.dtos

data class BankingPartnerBrcodePreview(
    val accountNumber: String,
    val accountType: String,
    val allowChange: Boolean,
    val amount: Long,
    val bankCode: String,
    val branchCode: String,
    val discountAmount: Double,
    val fineAmount: Double,
    val interestAmount: Double,
    val name: String,
    val nominalAmount: Double,
    val reconciliationId: String,
    val status: String,
    val taxId: String
)
