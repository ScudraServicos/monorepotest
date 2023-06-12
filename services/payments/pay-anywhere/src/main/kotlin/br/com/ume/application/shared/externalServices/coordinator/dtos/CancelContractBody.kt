package br.com.ume.application.shared.externalServices.coordinator.dtos

data class CancelContractBody(
    val reason: String,
    val requester: String
)