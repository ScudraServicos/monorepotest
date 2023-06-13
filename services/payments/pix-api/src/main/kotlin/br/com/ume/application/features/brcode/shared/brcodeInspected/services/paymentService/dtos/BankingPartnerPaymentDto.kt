package br.com.ume.application.features.brcode.shared.brcodeInspected.services.paymentService.dtos

data class BankingPartnerPaymentDto (
    val id: String,
    val brcode: String,
    val taxId: String,
    val description: String,
    val value: Double,
    val scheduled: String,
    val beneficiaryName: String?,
    val tags: List<String>,
    val status: String,
    val brcodeType: String
)