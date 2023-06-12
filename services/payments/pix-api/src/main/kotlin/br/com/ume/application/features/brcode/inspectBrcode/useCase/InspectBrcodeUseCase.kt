package br.com.ume.application.features.brcode.inspectBrcode.useCase

import br.com.ume.application.features.brcode.shared.brcodeInspected.domain.BrcodeInspected

interface InspectBrcodeUseCase {
    fun execute(brcode: String): BrcodeInspected
}