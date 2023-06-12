package br.com.ume.application.features.brcode.shared.services.brcodeValidation.interfaces

import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.PixApiInspectResponseDto

interface PaymentPermissionValidator {
    fun execute(brcode: PixApiInspectResponseDto, userId: String)
}