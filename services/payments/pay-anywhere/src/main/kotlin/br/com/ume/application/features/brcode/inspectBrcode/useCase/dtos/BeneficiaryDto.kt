package br.com.ume.application.features.brcode.inspectBrcode.useCase.dtos

data class BeneficiaryDto(
    val name: String,
    val document: String,
    val bankCode: String,
    val bankName: String,
    val pixKey: String
)
