package br.com.ume.application.shared.payment.repository

import br.com.ume.application.shared.payment.repository.dtos.PaymentDto
import io.micronaut.runtime.http.scope.RequestScope
import java.util.*

@RequestScope
class PaymentRepositoryImpl (
    private val paymentJpaRepository: PaymentJpaRepository
) : PaymentRepository {

    override fun createPayment(payment: PaymentDto): PaymentDto {
        return paymentJpaRepository.save(payment)
    }

    override fun findByBrCode(brCode: String): PaymentDto? {
        return paymentJpaRepository.findByBrCode(brCode)
    }

    override fun findByContractIdAndUserId(contractId: String, userId: String): PaymentDto? {
        return paymentJpaRepository.findByPaymentOriginContractIdAndPaymentOriginUserId(contractId, userId)
    }

    override fun findPayment(brCode: String, contractId: String): PaymentDto? {
        return paymentJpaRepository.findByBrCodeAndPaymentOriginContractId(brCode, contractId)
    }

    override fun findById(id: String): PaymentDto? {
        return paymentJpaRepository.findById(UUID.fromString(id)).orElse(null)
    }
}