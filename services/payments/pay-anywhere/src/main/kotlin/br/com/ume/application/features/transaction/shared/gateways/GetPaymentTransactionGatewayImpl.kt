package br.com.ume.application.features.transaction.shared.gateways

import br.com.ume.application.features.brcode.shared.services.pixApiService.PixApiService
import br.com.ume.application.features.brcode.shared.services.pixApiService.enums.PixApiTransactionErrorEnum
import br.com.ume.application.features.transaction.shared.builders.PaymentTransactionDtoBuilder
import br.com.ume.application.features.transaction.shared.gateways.dtos.GetPaymentTransactionOutput
import br.com.ume.application.features.transaction.shared.gateways.enums.GetPaymentTransactionErrorEnum
import br.com.ume.application.shared.payment.repository.PaymentRepository
import io.micronaut.runtime.http.scope.RequestScope

@RequestScope
class GetPaymentTransactionGatewayImpl(
    private val pixApiService: PixApiService,
    private val paymentRepository: PaymentRepository
) : GetPaymentTransactionGateway {
    override fun getTransactionByContractIdAndUserId(contractId: String, userId: String): GetPaymentTransactionOutput {
        val payment = paymentRepository.findByContractIdAndUserId(contractId, userId)
            ?: return GetPaymentTransactionOutput(error = GetPaymentTransactionErrorEnum.TRANSACTION_NOT_FOUND)

        val pixApiTransactionOutput = pixApiService.getTransaction(payment.id.toString())
        if (pixApiTransactionOutput.value == null) return GetPaymentTransactionOutput(error = mapPixApiError(pixApiTransactionOutput.error))

        return GetPaymentTransactionOutput(PaymentTransactionDtoBuilder.build(pixApiTransactionOutput.value, payment))
    }

    private fun mapPixApiError(pixApiError: PixApiTransactionErrorEnum?): GetPaymentTransactionErrorEnum {
        return when (pixApiError) {
            PixApiTransactionErrorEnum.TRANSACTION_NOT_FOUND -> GetPaymentTransactionErrorEnum.TRANSACTION_NOT_FOUND
            else -> GetPaymentTransactionErrorEnum.UNKNOWN_ERROR
        }
    }
}