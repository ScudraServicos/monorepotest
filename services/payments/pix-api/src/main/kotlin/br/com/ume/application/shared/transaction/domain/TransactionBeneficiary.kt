package br.com.ume.application.shared.transaction.domain

import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.enums.PixBeneficiaryType
import java.sql.Timestamp

data class TransactionBeneficiary(
    val id: String,
    val externalId: Long,
    val name: String,
    val legalNature: PixBeneficiaryType,
    val document: String,
    val pixKey: String,
    val bankIspbCode: String,
    val bankBranch: String,
    val bankAccount: String,
    var bankName: String,
    var accountType: String,
    val creationTimestamp: Timestamp,
    val updateTimestamp: Timestamp,
)
