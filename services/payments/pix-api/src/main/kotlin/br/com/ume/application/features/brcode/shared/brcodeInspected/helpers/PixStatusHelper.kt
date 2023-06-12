package br.com.ume.application.features.brcode.shared.brcodeInspected.helpers

import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.PixStatusEnum

abstract class PixStatusHelper {
    companion object {
        fun fromPartnerBrcodePreview(partnerStatus: String): PixStatusEnum {
            return when (partnerStatus) {
                "active" -> PixStatusEnum.ACTIVE
                "paid" -> PixStatusEnum.PAID
                "canceled" -> PixStatusEnum.CANCELED
                "expired" -> PixStatusEnum.EXPIRED
                "created" -> PixStatusEnum.CREATED
                else -> PixStatusEnum.UNKNOWN
            }
        }
    }
}