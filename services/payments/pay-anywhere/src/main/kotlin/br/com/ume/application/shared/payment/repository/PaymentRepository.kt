package br.com.ume.application.shared.payment.repository

import br.com.ume.application.shared.payment.repository.dtos.PaymentDto

interface PaymentRepository {
    fun createPayment(payment: PaymentDto): PaymentDto
    fun findByBrCode(brCode: String): PaymentDto?
    fun findByContractIdAndUserId(contractId: String, userId: String): PaymentDto?
    fun findPayment(brCode: String, contractId: String): PaymentDto?
    fun findById(id: String): PaymentDto?
}