package br.com.ume.application.features.brcode.inspectBrcode.useCase

import br.com.ume.application.features.brcode.inspectBrcode.useCase.dtos.InspectedBrcodeDto

interface InspectBrcodeUseCase {
    fun execute(brcode: String, userId: String): InspectedBrcodeDto
}