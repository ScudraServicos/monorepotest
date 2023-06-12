package br.com.ume.application.features.brcode.payBrcode.useCase

import br.com.ume.api.exceptions.types.InternalErrorException
import br.com.ume.application.features.brcode.payBrcode.errors.PayBrcodeErrorEnum
import br.com.ume.application.features.brcode.payBrcode.gateways.PayBrcodeGateway
import br.com.ume.application.features.brcode.payBrcode.useCase.dtos.TransactionOriginDto
import br.com.ume.application.features.brcode.shared.brcodeInspected.domain.BrcodeInspected
import br.com.ume.application.features.brcode.shared.brcodeInspected.gateways.brcodeInspection.BrcodeInspectionGateway
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.paymentService.dtos.BankingPartnerPaymentDto
import br.com.ume.application.shared.logging.gcp.JsonLogBuilder
import br.com.ume.application.shared.logging.gcp.LoggerFactory
import br.com.ume.application.shared.transaction.builders.CreateBrcodeTransactionDtoBuilder
import br.com.ume.application.shared.transaction.domain.Transaction
import br.com.ume.application.shared.transaction.enums.TransactionStatusEnum
import br.com.ume.application.shared.transaction.gateway.TransactionGateway
import io.micronaut.runtime.http.scope.RequestScope
import java.util.logging.Logger

@RequestScope
class PayBrcodeUseCaseImpl(
    private val payBrcodeGateway: PayBrcodeGateway,
    private val brcodeInspectionGateway: BrcodeInspectionGateway,
    private val transactionGateway: TransactionGateway
) : PayBrcodeUseCase {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(PayBrcodeUseCaseImpl::class.java.name)
    }

    override fun execute(brcode: String, transactionOrigin: TransactionOriginDto): Transaction {
        val inspectedBrcode = inspectBrcode(brcode)
        val transaction = findOrCreateTransaction(brcode, inspectedBrcode, transactionOrigin)

        val bankingPartnerPayment = findBankingPartnerPayment(transactionOrigin)
        if (bankingPartnerPayment == null) {
            val paymentId = payBrcode(brcode, inspectedBrcode, transactionOrigin)
            updateTransaction(transaction.id, TransactionStatusEnum.CREATED, paymentId)
        } else if (transaction.partnerExternalId == null) {
            val status = TransactionStatusEnum.fromBankingPartnerStatus(bankingPartnerPayment.status)!!
            updateTransaction(transaction.id, status, bankingPartnerPayment.id)
        }
        return transaction
    }

    private fun inspectBrcode(brcode: String): BrcodeInspected {
        val inspectedBrcode = brcodeInspectionGateway.inspectBrcode(brcode)
        if (inspectedBrcode.brcodeInspected == null) {
            log.severe(JsonLogBuilder.build(object {
                val message = "Brcode inspection error on payment"
                val error = inspectedBrcode.error
            }))
            throw InternalErrorException(inspectedBrcode.error.toString())
        }

        return inspectedBrcode.brcodeInspected
    }

    private fun findOrCreateTransaction(
        brcode: String,
        inspectedBrcode: BrcodeInspected,
        transactionOrigin: TransactionOriginDto
    ): Transaction {
        var transaction = transactionGateway.getTransaction(
            transactionOrigin.sourceProductReferenceName,
            transactionOrigin.sourceProductReferenceId
        )
        if (transaction == null) {
            val transactionId = createTransaction(brcode, inspectedBrcode, transactionOrigin)

            transaction = transactionGateway.getTransaction(transactionId)
            if (transaction == null) {
                log.severe(JsonLogBuilder.build(object {
                    val message = "Transaction not found after it's been created"
                    val transactionId = transactionId
                    val transactionOrigin = transactionOrigin
                }))
                throw InternalErrorException(PayBrcodeErrorEnum.TRANSACTION_NOT_FOUND.toString())
            }
        }
        return transaction
    }

    // TODO: Return the whole entity
    private fun createTransaction(
        brcode: String,
        inspectedBrcode: BrcodeInspected,
        transactionOrigin: TransactionOriginDto
    ): String {
        return transactionGateway.createBrcodeTransaction(
            CreateBrcodeTransactionDtoBuilder.build(brcode, inspectedBrcode, transactionOrigin)
        ) ?: throw InternalErrorException(PayBrcodeErrorEnum.TRANSACTION_CREATION_ERROR.toString())
    }

    private fun findBankingPartnerPayment(transactionOrigin: TransactionOriginDto): BankingPartnerPaymentDto? {
        return transactionGateway.getBankingPartnerPaymentByProduct(
            transactionOrigin.sourceProductReferenceId,
            transactionOrigin.sourceProductReferenceName
        )
    }

    private fun payBrcode(
        brcode: String,
        inspectedBrcode: BrcodeInspected,
        transactionOrigin: TransactionOriginDto
    ): String {
        return payBrcodeGateway.payBrcode(brcode, inspectedBrcode.pixBeneficiary, transactionOrigin)
            ?: throw InternalErrorException(PayBrcodeErrorEnum.PAYMENT_ERROR.toString())
    }

    private fun updateTransaction(
        transactionId: String,
        status: TransactionStatusEnum,
        paymentId: String
    ) {
        val successfulUpdate =
            transactionGateway.updateTransaction(transactionId, status, paymentId)
        if (!successfulUpdate) throw InternalErrorException(PayBrcodeErrorEnum.TRANSACTION_UPDATE_ERROR.toString())
    }
}