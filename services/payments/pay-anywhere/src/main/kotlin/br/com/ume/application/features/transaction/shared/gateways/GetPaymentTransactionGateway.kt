package br.com.ume.application.features.transaction.shared.gateways

import br.com.ume.application.features.transaction.shared.gateways.dtos.GetPaymentTransactionOutput

interface GetPaymentTransactionGateway {
    fun getTransactionByContractIdAndUserId(contractId: String, userId: String): GetPaymentTransactionOutput
}