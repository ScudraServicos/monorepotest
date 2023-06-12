package br.com.ume.application.features.brcode.payBrcode.gateways

import br.com.ume.application.features.brcode.payBrcode.useCase.dtos.TransactionOriginDto
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.paymentService.PaymentService
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.dtos.PixBeneficiaryDto
import io.micronaut.runtime.http.scope.RequestScope

@RequestScope
class PayBrcodeGatewayImpl(
    private val paymentService: PaymentService
) : PayBrcodeGateway {
    override fun payBrcode(brcode: String, beneficiaryDto: PixBeneficiaryDto, transactionOrigin: TransactionOriginDto): String? {
        return paymentService.payBrcode(brcode, beneficiaryDto.document, transactionOrigin)
    }
}