package br.com.ume.application.shared.externalServices.coordinator.dtos

import br.com.ume.application.shared.externalServices.coordinator.enums.SourceProductEnum

data class Contract(
    val id: String,
    val borrowerId: String,
    val storeId: String,
    val principal: Double,
    val operatorId: String?,
    val createdOn: String,
    val firstInstallmentDueDate: String,
    val debtFundingPackId: String,
    val principalInStore: Double,
    val canceledOn: String?,
    val sourceProduct: SourceProductEnum?,
    val proposals: List<Proposal>
)