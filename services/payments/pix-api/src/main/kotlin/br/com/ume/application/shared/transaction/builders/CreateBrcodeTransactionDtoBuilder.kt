package br.com.ume.application.shared.transaction.builders

import br.com.ume.application.features.brcode.payBrcode.useCase.dtos.TransactionOriginDto
import br.com.ume.application.features.brcode.shared.brcodeInspected.domain.BrcodeInspected
import br.com.ume.application.shared.transaction.domain.TransactionBeneficiary
import br.com.ume.application.shared.transaction.domain.TransactionOrigin
import br.com.ume.application.shared.transaction.gateway.dtos.CreateBrcodeTransactionDto
import br.com.ume.application.shared.transaction.gateway.dtos.CreateTransactionBeneficiaryDto
import br.com.ume.application.shared.transaction.gateway.dtos.CreateTransactionOriginDto
import br.com.ume.application.utils.utcNow

class CreateBrcodeTransactionDtoBuilder {
    companion object {
        fun build(
            brcode: String,
            brcodeInspected: BrcodeInspected,
            transactionOrigin: TransactionOriginDto
        ): CreateBrcodeTransactionDto {
            val utcNow = utcNow()
            return CreateBrcodeTransactionDto(
                value = brcodeInspected.value,
                brcode = brcode,
                txId = brcodeInspected.txId,
                beneficiary = CreateTransactionBeneficiaryDto(
                    pixKey = brcodeInspected.pixBeneficiary.pixKey,
                    bankIspbCode = brcodeInspected.pixBeneficiary.bankingAccount.bankCode,
                    bankAccount = brcodeInspected.pixBeneficiary.bankingAccount.accountNumber,
                    bankBranch = brcodeInspected.pixBeneficiary.bankingAccount.branchCode,
                    name = brcodeInspected.pixBeneficiary.name,
                    legalNature = brcodeInspected.pixBeneficiary.type,
                    document = brcodeInspected.pixBeneficiary.document,
                    bankName = brcodeInspected.pixBeneficiary.bankingAccount.bankName,
                    accountType = brcodeInspected.pixBeneficiary.bankingAccount.accountType
                ),
                transactionOrigin = CreateTransactionOriginDto(
                    sourceProductReferenceName = transactionOrigin.sourceProductReferenceName,
                    sourceProductReferenceId = transactionOrigin.sourceProductReferenceId,
                    userId = transactionOrigin.userId,
                ),
                creationTimestamp = utcNow,
                updateTimestamp = utcNow,
            )
        }
    }
}