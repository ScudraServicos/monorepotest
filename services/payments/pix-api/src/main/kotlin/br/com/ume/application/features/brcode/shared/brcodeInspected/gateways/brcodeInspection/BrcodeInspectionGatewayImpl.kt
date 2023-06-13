package br.com.ume.application.features.brcode.shared.brcodeInspected.gateways.brcodeInspection

import br.com.ume.application.features.brcode.shared.brcodeInspected.builders.BrcodeInspectedBuilder
import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.PixTypeEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.extensions.decodedBrcodeDto.getPixType
import br.com.ume.application.features.brcode.shared.brcodeInspected.gateways.brcodeInspection.dtos.InspectBrcodeOutput
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeEnrichingService.BrcodeEnrichingService
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeEnrichingService.dtos.BrcodePayloadDto
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeEnrichingService.dtos.GetBrcodePayloadOutput
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.BrcodeService
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.dtos.DecodedBrcodeDto
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.PixBeneficiaryService
import io.micronaut.runtime.http.scope.RequestScope

@RequestScope
class BrcodeInspectionGatewayImpl(
    private val brcodeService: BrcodeService,
    private val brcodeEnrichingService: BrcodeEnrichingService,
    private val pixBeneficiaryService: PixBeneficiaryService
): BrcodeInspectionGateway {
    override fun inspectBrcode(brcode: String): InspectBrcodeOutput {
        val decodedBrcode = brcodeService.decodeBrcode(brcode)
        if (decodedBrcode.value == null) return InspectBrcodeOutput(error = decodedBrcode.error)

        val brcodePreview = brcodeService.getBrcodePreview(brcode)
        if (brcodePreview.value == null) return InspectBrcodeOutput(error = brcodePreview.error)

        val brcodePayload = getBrcodePayload(decodedBrcode.value)
        if (brcodePayload?.error != null) return InspectBrcodeOutput(error = brcodePayload.error)

        val pixKey = getPixKey(decodedBrcode.value, brcodePayload?.value)
            ?: return InspectBrcodeOutput(error = InspectBrcodeErrorEnum.PIX_KEY_NOT_FOUND)

        val pixBeneficiary = pixBeneficiaryService.getBeneficiary(pixKey)
        if (pixBeneficiary.value == null) return InspectBrcodeOutput(error = pixBeneficiary.error)

        val inspectedBrcode = BrcodeInspectedBuilder.buildOutput(
            decodedBrcode.value, brcodePreview.value, brcodePayload?.value, pixBeneficiary.value
        )

        return InspectBrcodeOutput(inspectedBrcode)
    }

    private fun getBrcodePayload(decodedBrcode: DecodedBrcodeDto): GetBrcodePayloadOutput? {
        return if (decodedBrcode.getPixType() == PixTypeEnum.DYNAMIC) brcodeEnrichingService.getBrcodePayload(decodedBrcode.merchantUrl!!)
        else null
    }

    private fun getPixKey(decodedBrcodeDto: DecodedBrcodeDto, brcodePayloadDto: BrcodePayloadDto?): String? {
        if (brcodePayloadDto?.chave != null) return brcodePayloadDto.chave
        return decodedBrcodeDto.pixKey
    }
}