package br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService

import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.dtos.DecodeBrcodeOutput
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.dtos.GetBrcodePreviewOutput

interface BrcodeService {
    fun decodeBrcode(brcode: String): DecodeBrcodeOutput
    fun getBrcodePreview(brcode: String): GetBrcodePreviewOutput
}