package br.com.ume.application.shared.transaction.testBuilders

import br.com.ume.application.features.brcode.shared.brcodeInspected.services.paymentService.dtos.BankingPartnerPaymentDto
import java.time.LocalDateTime

abstract class BankingPartnerPaymentDtoTestBuilder {
    companion object {
        fun build(): BankingPartnerPaymentDto {
            return BankingPartnerPaymentDto(
                "123",
                "brcode",
                "taxId",
                "description",
                12.12,
                LocalDateTime.now().toString(),
                "Tony",
                listOf(),
                "success",
                brcodeType = "dynamic"
            )
        }
    }
}