package br.com.ume.application.brcode.shared.testBuilders

import br.com.ume.application.features.brcode.shared.brcodeInspected.wrappers.bankingPartner.dtos.BankingPartnerBrcodePreview

class BankingPartnerBrcodePreviewBuilder {
    companion object {
        fun buildActiveBrcodePreview(): BankingPartnerBrcodePreview {
            return BankingPartnerBrcodePreview(
                "4324051749321261",
                "salary",
                false,
                1500,
                "17343510",
                "8632",
                0.0,
                0.0,
                0.0,
                "Eric Forman",
                0.0,
                "12345",
                "active",
                "***.703.502-**",
            )
        }
    }
}