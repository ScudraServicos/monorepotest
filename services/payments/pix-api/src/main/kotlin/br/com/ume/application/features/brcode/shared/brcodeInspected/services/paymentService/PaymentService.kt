package br.com.ume.application.features.brcode.shared.brcodeInspected.services.paymentService

import br.com.ume.application.features.brcode.payBrcode.useCase.dtos.TransactionOriginDto
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.paymentService.dtos.BankingPartnerPaymentDto

interface PaymentService {
    fun payBrcode(brcode: String, beneficiaryDocument: String, transactionOrigin: TransactionOriginDto): String?
    fun getBrcodePaymentByProduct(productId: String, sourceProduct: String): BankingPartnerPaymentDto?
}