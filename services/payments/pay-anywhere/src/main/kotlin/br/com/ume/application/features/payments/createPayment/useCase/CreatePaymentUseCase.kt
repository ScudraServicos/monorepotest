package br.com.ume.application.features.payments.createPayment.useCase

import br.com.ume.application.shared.payment.repository.dtos.PaymentDto

interface CreatePaymentUseCase {
    fun execute(
            proposalId: String,
            contractId: String,
            brCodeString: String,
            userId: String,
            headers: Map<String, String>
    ): PaymentDto
}