package br.com.ume.application.features.transaction.getTransaction.useCase

import br.com.ume.api.exceptions.types.InternalErrorException
import br.com.ume.api.exceptions.types.NotFoundException
import br.com.ume.application.features.transaction.shared.dtos.PaymentTransactionDto
import br.com.ume.application.features.transaction.shared.gateways.GetPaymentTransactionGateway
import br.com.ume.application.features.transaction.shared.gateways.enums.GetPaymentTransactionErrorEnum
import io.micronaut.runtime.http.scope.RequestScope

@RequestScope
class GetTransactionUseCaseImpl(
    private val getTransactionGateway: GetPaymentTransactionGateway
) : GetTransactionUseCase {
    override fun execute(contractId: String, userId: String): PaymentTransactionDto {
        val getTransactionOutput = getTransactionGateway.getTransactionByContractIdAndUserId(contractId, userId)
        if (getTransactionOutput.value == null) throw getThrowable(getTransactionOutput.error)

        return getTransactionOutput.value
    }

    private fun getThrowable(getTransactionError: GetPaymentTransactionErrorEnum?): Throwable {
        return when (getTransactionError) {
            GetPaymentTransactionErrorEnum.TRANSACTION_NOT_FOUND -> NotFoundException(GetPaymentTransactionErrorEnum.TRANSACTION_NOT_FOUND.toString())
            else -> InternalErrorException(GetPaymentTransactionErrorEnum.UNKNOWN_ERROR.toString())
        }
    }
}