package br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.transactions

import br.com.ume.application.shared.enums.PixPaymentStatusEnum
import java.sql.Timestamp

data class PixApiTransactionResponseDto(
    val value: Double,
    val brcode: String?,
    val beneficiary: PixApiTransactionBeneficiaryDto,
    val txId: String?,
    val status: PixPaymentStatusEnum,
    val creationTimestamp: Timestamp,
    val updateTimestamp: Timestamp,
    val origin: PixApiTransactionOriginDto?,
)
