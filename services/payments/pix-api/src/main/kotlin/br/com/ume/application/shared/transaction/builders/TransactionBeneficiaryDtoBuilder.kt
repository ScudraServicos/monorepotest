package br.com.ume.application.shared.transaction.builders

import br.com.ume.application.shared.transaction.domain.TransactionBeneficiary
import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionBeneficiaryDto
import java.util.*

class TransactionBeneficiaryDtoBuilder {
    companion object {
        fun buildFromTransactionBeneficiary(transactionBeneficiary: TransactionBeneficiary): TransactionBeneficiaryDto {
            return TransactionBeneficiaryDto(
                id = UUID.fromString(transactionBeneficiary.id),
                externalId = transactionBeneficiary.externalId,
                name = transactionBeneficiary.name,
                legalNature = transactionBeneficiary.legalNature.toString(),
                document = transactionBeneficiary.document,
                pixKey = transactionBeneficiary.pixKey,
                bankIspbCode = transactionBeneficiary.bankIspbCode,
                bankAccount = transactionBeneficiary.bankAccount,
                bankBranch = transactionBeneficiary.bankBranch,
                bankName = transactionBeneficiary.bankName,
                accountType = transactionBeneficiary.accountType,
                creationTimestamp = transactionBeneficiary.creationTimestamp,
                updateTimestamp = transactionBeneficiary.updateTimestamp
            )
        }
    }
}