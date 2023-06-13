package br.com.ume.application.features.brcode.shared.brcodeInspected.services.paymentService.builders

import br.com.ume.application.features.brcode.shared.brcodeInspected.services.paymentService.dtos.BankingPartnerPaymentDto
import br.com.ume.application.features.brcode.shared.brcodeInspected.wrappers.bankingPartner.utils.parsePartnerAmount
import com.starkbank.BrcodePayment

abstract class BankingPartnerPaymentDtoBuilder {
    companion object {
        fun fromBankingPartnerBrcodePayment(brcodePayment: BrcodePayment): BankingPartnerPaymentDto {
            return BankingPartnerPaymentDto(
                id = brcodePayment.id,
                brcode = brcodePayment.brcode,
                taxId = brcodePayment.taxId,
                description = brcodePayment.description,
                value = parsePartnerAmount(brcodePayment.amount),
                scheduled = brcodePayment.scheduled,
                beneficiaryName = brcodePayment.name,
                tags = brcodePayment.tags.toList(),
                status = brcodePayment.status,
                brcodeType = brcodePayment.type
            )
        }
    }
}
