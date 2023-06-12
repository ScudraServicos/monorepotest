package br.com.ume.application.features.brcode.payBrcode.gateways

import br.com.ume.application.features.brcode.payBrcode.useCase.dtos.TransactionOriginDto
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.dtos.PixBeneficiaryDto

interface PayBrcodeGateway {
    fun payBrcode(brcode: String, beneficiaryDto: PixBeneficiaryDto, transactionOrigin: TransactionOriginDto): String?
}