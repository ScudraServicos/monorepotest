package br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.transactions

import br.com.ume.application.shared.enums.PixBeneficiaryTypeEnum

data class PixApiTransactionBeneficiaryDto(
    val name: String,
    val legalNature: PixBeneficiaryTypeEnum,
    val document: String,
    val pixKey: String,
    val bankBranch: String,
    val bankAccount: String,
    val bankName: String,
    val accountType: String,
)
