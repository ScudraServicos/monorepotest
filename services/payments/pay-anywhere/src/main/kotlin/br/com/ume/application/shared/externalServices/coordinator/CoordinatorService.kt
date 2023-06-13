package br.com.ume.application.shared.externalServices.coordinator

import br.com.ume.application.shared.externalServices.coordinator.dtos.Contract

interface CoordinatorService {
    fun getContract(contractId: String, headers: Map<String, String>): Contract?
    fun acceptProposal(proposalId: String, headers: Map<String, String>): Contract?
    fun cancelContract(contractId: String, reason: String): Boolean
}