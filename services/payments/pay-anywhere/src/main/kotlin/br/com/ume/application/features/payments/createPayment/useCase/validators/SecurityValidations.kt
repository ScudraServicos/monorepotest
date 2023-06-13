package br.com.ume.application.features.payments.createPayment.useCase.validators

import br.com.ume.api.exceptions.types.BusinessRuleException
import br.com.ume.application.features.brcode.inspectBrcode.useCase.dtos.InspectedBrcodeDto
import br.com.ume.application.features.payments.createPayment.useCase.enums.ProcessPaymentErrorEnum
import br.com.ume.application.shared.externalServices.coordinator.dtos.Contract

fun validatePayment(proposalId: String, contract: Contract, brCode: InspectedBrcodeDto) {
    validateProposalBelongsToContract(proposalId, contract)
    validatePaymentValue(contract.principal, brCode.value)
}

fun validatePaymentValue(contractValue: Number, brCodeValue: Number) {
    if (contractValue != brCodeValue)
        throw BusinessRuleException(ProcessPaymentErrorEnum.INVALID_PAYMENT_VALUE.toString())
}

fun validateProposalBelongsToContract(proposalId: String, contract: Contract) {
    val proposalBelongsToContract = contract.proposals.any { it.id == proposalId }
    if (!proposalBelongsToContract)
        throw BusinessRuleException(ProcessPaymentErrorEnum.PROPOSAL_DOES_NOT_BELONG_TO_CONTRACT.toString())
}