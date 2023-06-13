package br.com.ume.application.shared.payment.gateway

import br.com.ume.application.shared.payment.repository.PaymentRepository
import br.com.ume.application.shared.payment.repository.dtos.PaymentDto
import io.micronaut.runtime.http.scope.RequestScope

@RequestScope
class PaymentGatewayImpl(
    private val paymentRepository: PaymentRepository
) : PaymentGateway {
    override fun findPayment(id: String): PaymentDto? {
        return this.paymentRepository.findById(id)
    }
}