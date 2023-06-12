package br.com.ume.application.shared.transaction.builders

import br.com.ume.application.shared.transaction.domain.Transaction
import br.com.ume.application.shared.transaction.enums.TransactionStatusEnum
import br.com.ume.application.shared.transaction.enums.TransactionTypeEnum
import br.com.ume.application.shared.transaction.gateway.dtos.CreateBrcodeTransactionDto
import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionBeneficiaryDto
import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionDto
import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionOriginDto
import br.com.ume.application.utils.utcNow
import java.util.*

class TransactionDtoBuilder {
    companion object {
        fun buildBRCodeTransaction(createBrcodeTransactionDto: CreateBrcodeTransactionDto): TransactionDto {
            val utcNow = utcNow()
            return TransactionDto(
                value = createBrcodeTransactionDto.value,
                type = TransactionTypeEnum.BRCODE_PAYMENT.toString(),
                txId = createBrcodeTransactionDto.txId,
                partnerExternalId = createBrcodeTransactionDto.partnerExternalId,
                brcode = createBrcodeTransactionDto.brcode,
                status = TransactionStatusEnum.PENDING_CREATION.toString(),
                origin = TransactionOriginDto(
                    sourceProductReferenceName = createBrcodeTransactionDto.transactionOrigin.sourceProductReferenceName,
                    sourceProductReferenceId = createBrcodeTransactionDto.transactionOrigin.sourceProductReferenceId,
                    userId = createBrcodeTransactionDto.transactionOrigin.userId,
                    creationTimestamp = utcNow,
                    updateTimestamp = utcNow,
                ),
                beneficiary = TransactionBeneficiaryDto(
                    name = createBrcodeTransactionDto.beneficiary.name,
                    legalNature = createBrcodeTransactionDto.beneficiary.legalNature.toString(),
                    document = createBrcodeTransactionDto.beneficiary.document,
                    pixKey = createBrcodeTransactionDto.beneficiary.pixKey,
                    bankIspbCode = createBrcodeTransactionDto.beneficiary.bankIspbCode,
                    bankAccount = createBrcodeTransactionDto.beneficiary.bankAccount,
                    bankBranch = createBrcodeTransactionDto.beneficiary.bankBranch,
                    creationTimestamp = utcNow,
                    updateTimestamp = utcNow,
                    bankName = createBrcodeTransactionDto.beneficiary.bankName,
                    accountType = createBrcodeTransactionDto.beneficiary.accountType.toString()
                ),
                creationTimestamp = utcNow,
                updateTimestamp = utcNow,
            )
        }
        fun buildFromTransaction(transaction: Transaction): TransactionDto {
            return TransactionDto(
                id = UUID.fromString(transaction.id),
                externalId = transaction.externalId,
                value = transaction.value,
                type = transaction.type.toString(),
                partnerExternalId = transaction.partnerExternalId,
                txId = transaction.txId,
                brcode = transaction.brcode,
                status = transaction.status.toString(),
                beneficiary = TransactionBeneficiaryDtoBuilder.buildFromTransactionBeneficiary(transaction.beneficiary),
                origin = TransactionOriginDtoBuilder.buildFromTransactionOrigin(transaction.origin),
                creationTimestamp = transaction.creationTimestamp,
                updateTimestamp = transaction.updateTimestamp
            )
        }
    }
}