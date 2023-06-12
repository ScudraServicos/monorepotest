package br.com.ume.application.shared.testBuilders

import br.com.ume.application.shared.externalServices.coordinator.dtos.Contract
import br.com.ume.application.shared.externalServices.coordinator.dtos.Proposal
import br.com.ume.application.shared.externalServices.coordinator.enums.SourceProductEnum

class ContractBuilder {
    companion object {
        fun buildContract(
            contractId: String,
            borrowerId: String,
            proposals: List<Proposal>,
        ): Contract {
            return Contract(
                id = contractId,
                borrowerId,
                storeId = "1",
                principal = 10.0,
                operatorId = null,
                createdOn = "2023-01-01 23:17:00",
                firstInstallmentDueDate = "2023-02-01",
                debtFundingPackId = "1",
                principalInStore = 20.0,
                canceledOn = null,
                sourceProduct = SourceProductEnum.PAY_ANYWHERE,
                proposals,
            )
        }

        fun buildProposal(
            proposalId: String,
            contractId: String,
            installmentValue: Double = 11.0,
            wasAccepted: Boolean = false,
        ): Proposal {
            return Proposal(
                id = proposalId,
                contractId = contractId,
                numberOfInstallments = 1,
                installmentValue,
                wasAccepted = wasAccepted,
            )
        }
    }
}