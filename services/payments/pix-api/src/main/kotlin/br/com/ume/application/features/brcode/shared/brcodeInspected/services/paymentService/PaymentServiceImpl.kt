package br.com.ume.application.features.brcode.shared.brcodeInspected.services.paymentService

import br.com.ume.application.features.brcode.payBrcode.useCase.dtos.TransactionOriginDto
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.paymentService.builders.BankingPartnerPaymentDtoBuilder
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.paymentService.dtos.BankingPartnerPaymentDto
import br.com.ume.application.features.brcode.shared.brcodeInspected.wrappers.bankingPartner.BankingPartnerWrapper
import br.com.ume.application.shared.logging.gcp.JsonLogBuilder
import br.com.ume.application.shared.logging.gcp.LoggerFactory
import br.com.ume.application.shared.transaction.helpers.TransactionTagHelper
import com.starkbank.error.InputErrors as BankingPartnerInputErrors
import com.starkbank.error.InternalServerError as BankingPartnerInternalServerError
import io.micronaut.runtime.http.scope.RequestScope
import java.util.logging.Logger

@RequestScope
class PaymentServiceImpl(
    private val bankingPartnerWrapper: BankingPartnerWrapper
) : PaymentService {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(PaymentServiceImpl::class.java.name)
    }

    override fun payBrcode(brcode: String, beneficiaryDocument: String, transactionOrigin: TransactionOriginDto): String? {
        val tag = TransactionTagHelper.getTagFromProduct(
            transactionOrigin.sourceProductReferenceId,
            transactionOrigin.sourceProductReferenceName
        )

        return try {
            val payment = bankingPartnerWrapper.payBrcode(brcode, beneficiaryDocument, listOf(tag))

            payment.id
        } catch (ex: BankingPartnerInputErrors) {
            log.severe(JsonLogBuilder.build(object {
                val message = "BANKING_PARTNER_INVALID_PAYMENT"
                val exception = ex
            }))
            null
        } catch (ex: BankingPartnerInternalServerError) {
            log.severe(JsonLogBuilder.build(object {
                val message = "BANKING_PARTNER_ERROR"
                val exception = ex
            }))
            null
        }
    }

    override fun getBrcodePaymentByProduct(productId: String, sourceProduct: String): BankingPartnerPaymentDto? {
        val tag = TransactionTagHelper.getTagFromProduct(productId, sourceProduct)
        val payment = bankingPartnerWrapper.getBrcodePaymentByTag(tag) ?: return null

        return BankingPartnerPaymentDtoBuilder.fromBankingPartnerBrcodePayment(payment)
    }
}