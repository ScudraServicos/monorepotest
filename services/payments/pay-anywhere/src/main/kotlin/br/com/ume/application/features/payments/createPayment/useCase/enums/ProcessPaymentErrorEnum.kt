package br.com.ume.application.features.payments.createPayment.useCase.enums

enum class ProcessPaymentErrorEnum {
    INVALID_PAYMENT_VALUE,
    CONTRACT_NOT_FOUND,
    PROPOSAL_DOES_NOT_BELONG_TO_CONTRACT,
    ERROR_ACCEPTING_PROPOSAL,
}
