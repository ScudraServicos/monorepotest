package br.com.ume.application.features.brcode.shared.brcodeInspected.extensions.decodedBrcodeDto

import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.PixTypeEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeService.dtos.DecodedBrcodeDto

fun DecodedBrcodeDto.getPixType(): PixTypeEnum {
    return if (merchantUrl != null) PixTypeEnum.DYNAMIC else PixTypeEnum.STATIC
}