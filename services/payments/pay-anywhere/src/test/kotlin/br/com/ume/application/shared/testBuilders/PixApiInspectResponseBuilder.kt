package br.com.ume.application.shared.testBuilders

import br.com.ume.application.features.brcode.inspectBrcode.enums.PixStatusEnum
import br.com.ume.application.features.brcode.inspectBrcode.enums.PixTypeEnum
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.PixApiInspectResponseDto
import java.time.LocalDateTime
import java.time.temporal.TemporalAmount

class PixApiInspectResponseBuilder {
    companion object {
        fun buildStatic(
            status: PixStatusEnum = PixStatusEnum.ACTIVE,
            value: Double = 25.0,
            allowAlteration: Boolean = false,
            txId: String? = null,
            pixTypeEnum: PixTypeEnum = PixTypeEnum.STATIC
        ): PixApiInspectResponseDto {
            return PixApiInspectResponseDto(
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
                pixBeneficiary = PixApiInspectBeneficiaryBuilder.buildLegalPerson()
            )
        }

        fun buildDynamic(
            status: PixStatusEnum = PixStatusEnum.ACTIVE,
            value: Double = 25.0,
            allowAlteration: Boolean = false,
        ): PixApiInspectResponseDto {
            return PixApiInspectResponseDto(
                status = status,
                value = value,
                allowAlteration = allowAlteration,
                txId = "123-321-231-213",
                pixType = PixTypeEnum.DYNAMIC,
                createdAt = LocalDateTime.now(),
                presentedAt = LocalDateTime.now(),
                expiresAt = LocalDateTime.now().plusSeconds(120),
                dueDate = null,
                withdrawInfo = null,
                changeInfo = null,
                pixBeneficiary = PixApiInspectBeneficiaryBuilder.buildLegalPerson()
            )
        }
    }
}