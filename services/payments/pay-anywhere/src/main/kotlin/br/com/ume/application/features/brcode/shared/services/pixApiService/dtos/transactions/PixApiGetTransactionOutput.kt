package br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.transactions

import br.com.ume.application.features.brcode.shared.services.pixApiService.enums.PixApiTransactionErrorEnum

data class PixApiGetTransactionOutput(
    val value: PixApiTransactionResponseDto? = null,
    val error: PixApiTransactionErrorEnum? = null
)
