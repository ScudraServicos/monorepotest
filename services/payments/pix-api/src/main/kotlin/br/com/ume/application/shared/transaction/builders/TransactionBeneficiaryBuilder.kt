package br.com.ume.application.shared.transaction.builders

import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.enums.PixBeneficiaryType
import br.com.ume.application.shared.transaction.domain.TransactionBeneficiary
import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionBeneficiaryDto

abstract class TransactionBeneficiaryBuilder {
    companion object {
        fun buildFromEntity(beneficiaryEntity: TransactionBeneficiaryDto): TransactionBeneficiary {
            return TransactionBeneficiary(
                id = beneficiaryEntity.id.toString(),
                externalId = beneficiaryEntity.externalId!!,
                name = beneficiaryEntity.name,
                legalNature = PixBeneficiaryType.valueOf(beneficiaryEntity.legalNature),
                document = beneficiaryEntity.document,
                bankIspbCode = beneficiaryEntity.bankIspbCode,
                bankBranch = beneficiaryEntity.bankBranch,
                bankAccount = beneficiaryEntity.bankAccount,
                bankName = beneficiaryEntity.bankName,
                accountType = beneficiaryEntity.accountType,
                pixKey = beneficiaryEntity.pixKey,
                creationTimestamp = beneficiaryEntity.creationTimestamp,
                updateTimestamp = beneficiaryEntity.updateTimestamp
            )
        }
    }
}