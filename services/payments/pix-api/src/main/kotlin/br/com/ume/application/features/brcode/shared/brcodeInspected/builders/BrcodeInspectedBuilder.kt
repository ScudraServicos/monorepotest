package br.com.ume.application.features.brcode.shared.brcodeInspected.builders

import br.com.ume.application.features.brcode.shared.brcodeInspected.extensions.decodedBrcodeDto.getPixType
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.dtos.DecodedBrcodeDto
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeEnrichingService.dtos.BrcodePayloadDto
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.dtos.BrcodePreviewDto
import br.com.ume.application.features.brcode.shared.brcodeInspected.domain.BrcodeInspected
import br.com.ume.application.features.brcode.shared.brcodeInspected.domain.BrcodeInspectedWithdrawInfo
import br.com.ume.application.features.brcode.shared.brcodeInspected.domain.BrcodeInspectedChangeInfo
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.dtos.PixBeneficiaryDto
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

abstract class BrcodeInspectedBuilder {
    companion object {
        fun buildOutput(
            decodedBrcode: DecodedBrcodeDto,
            brcodePreview: BrcodePreviewDto,
            brcodePayload: BrcodePayloadDto?,
            pixBeneficiaryDto: PixBeneficiaryDto
        ): BrcodeInspected {
            val createdAt = getCreatedAt(brcodePayload)

            return BrcodeInspected(
                status = brcodePreview.status,
                pixType = decodedBrcode.getPixType(),
                value = brcodePreview.value,
                allowAlteration = brcodePreview.allowAlteration,
                txId = brcodePreview.txId,
                createdAt = createdAt,
                presentedAt = getPresentedAt(brcodePayload),
                expiresAt = getExpiresAt(createdAt, brcodePayload),
                dueDate = getDueDate(brcodePayload),
                withdrawInfo = buildSaque(brcodePayload),
                changeInfo = buildTroco(brcodePayload),
                pixBeneficiary = pixBeneficiaryDto
            )
        }

        private fun getCreatedAt(brcodePayload: BrcodePayloadDto?): LocalDateTime? {
            return if (brcodePayload != null)
                LocalDateTime.parse(brcodePayload.calendario.criacao, DateTimeFormatter.ISO_DATE_TIME) else null
        }

        private fun getPresentedAt(brcodePayload: BrcodePayloadDto?): LocalDateTime? {
            return if (brcodePayload != null)
                LocalDateTime.parse(brcodePayload.calendario.apresentacao, DateTimeFormatter.ISO_DATE_TIME) else null
        }

        private fun getExpiresAt(createdAt: LocalDateTime?, brcodePayload: BrcodePayloadDto?): LocalDateTime? {
            if (brcodePayload == null || createdAt == null) return null

            return createdAt.plusSeconds(brcodePayload.calendario.expiracao?.toLong() ?: 86400)
        }

        private fun getDueDate(brcodePayload: BrcodePayloadDto?): LocalDate? {
            val dueDate = brcodePayload?.calendario?.dataDeVencimento ?: return null
            return LocalDate.parse(dueDate)
        }

        private fun buildSaque(brcodePayload: BrcodePayloadDto?): BrcodeInspectedWithdrawInfo? {
            val saque = brcodePayload?.valor?.retirada?.saque ?: return null

            return BrcodeInspectedWithdrawInfo(value = saque.valor.toDouble())
        }

        private fun buildTroco(brCodePayload: BrcodePayloadDto?): BrcodeInspectedChangeInfo? {
            val troco = brCodePayload?.valor?.retirada?.troco ?: return null

            return BrcodeInspectedChangeInfo(value = troco.valor.toDouble())
        }
    }
}