package br.com.ume.application.shared.externalServices.coordinator.dtos

data class Proposal(
    val id: String,
    val contractId: String,
    val numberOfInstallments: Int,
    val installmentValue: Double,
    val wasAccepted: Boolean,
)