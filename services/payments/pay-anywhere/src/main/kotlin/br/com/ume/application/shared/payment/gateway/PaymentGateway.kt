package br.com.ume.application.shared.payment.gateway

import br.com.ume.application.shared.payment.repository.dtos.PaymentDto

interface PaymentGateway {
    fun findPayment(id: String): PaymentDto?
}