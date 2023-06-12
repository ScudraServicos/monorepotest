package br.com.ume.application.shared.payment.repository

import br.com.ume.application.shared.payment.repository.dtos.PaymentDto
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.UUID

@Repository
interface PaymentJpaRepository: JpaRepository<PaymentDto, UUID> {
    fun findByBrCode(brCode: String): PaymentDto?
    fun findByPaymentOriginContractId(contractId: String): PaymentDto?
    fun findByBrCodeAndPaymentOriginContractId(brCode: String, contractId: String): PaymentDto?
    fun findByPaymentOriginContractIdAndPaymentOriginUserId(contractId: String, userId: String): PaymentDto?
}