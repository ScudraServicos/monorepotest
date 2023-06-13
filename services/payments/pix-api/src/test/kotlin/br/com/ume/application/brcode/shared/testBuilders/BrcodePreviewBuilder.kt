package br.com.ume.application.brcode.shared.testBuilders

import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.PixStatusEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.dtos.BrcodePreviewDto

class BrcodePreviewBuilder {
    companion object {
        fun build(
            status: PixStatusEnum = PixStatusEnum.ACTIVE,
            value: Double = 25.0,
            allowAlteration: Boolean = false,
            txId: String? = null
        ): BrcodePreviewDto {
            return BrcodePreviewDto(
                status = status,
                value = value,
                allowAlteration = allowAlteration,
                txId = txId
            )
        }
    }
}