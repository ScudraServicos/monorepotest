package br.com.ume.application.features.transaction.updateTransactionStatus.useCase.utils

import br.com.ume.application.shared.transaction.enums.TransactionStatusEnum

private val updatableStatusForPendingCreation = hashSetOf(
    TransactionStatusEnum.CREATED,
    TransactionStatusEnum.PROCESSING,
    TransactionStatusEnum.CANCELED,
    TransactionStatusEnum.FAILED,
    TransactionStatusEnum.REFUNDED,
    TransactionStatusEnum.SUCCESS
)
private val updatableStatusForCreated = hashSetOf(
    TransactionStatusEnum.PROCESSING,
    TransactionStatusEnum.CANCELED,
    TransactionStatusEnum.FAILED,
    TransactionStatusEnum.REFUNDED,
    TransactionStatusEnum.SUCCESS
)
private val updatableStatusForProcessing = hashSetOf(
    TransactionStatusEnum.CANCELED,
    TransactionStatusEnum.FAILED,
    TransactionStatusEnum.REFUNDED,
    TransactionStatusEnum.SUCCESS
)
private val updatableStatusForSuccess = hashSetOf(
    TransactionStatusEnum.REFUNDED
)

fun isStatusUpdatable(currentStatus: TransactionStatusEnum, newStatus: TransactionStatusEnum): Boolean {
    return when (currentStatus) {
        TransactionStatusEnum.PENDING_CREATION -> updatableStatusForPendingCreation.contains(newStatus)
        TransactionStatusEnum.CREATED -> updatableStatusForCreated.contains(newStatus)
        TransactionStatusEnum.PROCESSING -> updatableStatusForProcessing.contains(newStatus)
        TransactionStatusEnum.SUCCESS -> updatableStatusForSuccess.contains(newStatus)
        TransactionStatusEnum.CANCELED -> false
        TransactionStatusEnum.FAILED -> false
        TransactionStatusEnum.REFUNDED -> false
    }
}