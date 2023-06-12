package br.com.ume.application.features.brcode.shared.services.brcodeValidation

import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.PixApiInspectResponseDto

interface BrcodeValidationService {
    fun validate(brcode: PixApiInspectResponseDto, userId: String)
}