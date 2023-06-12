package br.com.ume.application.brcode.shared.testBuilders

import br.com.ume.application.features.brcode.shared.brcodeInspected.domain.BrcodeInspected
import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.PixStatusEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.PixTypeEnum

class BrcodeInspectedBuilder {
    companion object {
        fun buildStatic(
            status: PixStatusEnum = PixStatusEnum.ACTIVE,
            value: Double = 25.0,
            allowAlteration: Boolean = false,
            txId: String? = null,
            pixTypeEnum: PixTypeEnum = PixTypeEnum.STATIC
        ): BrcodeInspected {
            return BrcodeInspected(
                status = status,
                value = value,
                allowAlteration = allowAlteration,
                txId = txId,
                pixType = pixTypeEnum,
                createdAt = null,
                presentedAt = null,
                expiresAt = null,
                dueDate = null,
                withdrawInfo = null,
                changeInfo = null,
                pixBeneficiary = PixBeneficiaryBuilder.buildLegalPerson()
            )
        }
    }
}