package br.com.ume.application.features.brcode.shared.services.pixApiService.dtos

import br.com.ume.application.features.brcode.shared.services.pixApiService.enums.PixApiInspectErrorEnum

data class PixApiInspectOutput(
    val value: PixApiInspectResponseDto? = null,
    val error: PixApiInspectErrorEnum? = null
)