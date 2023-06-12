package br.com.ume.application.features.payments.createPayment.useCase

import br.com.ume.api.exceptions.types.BusinessRuleException
import br.com.ume.api.exceptions.types.InternalErrorException
import br.com.ume.api.exceptions.types.NotFoundException
import br.com.ume.application.features.brcode.inspectBrcode.gateway.inspectBrcode.InspectBrcodeGatewayImpl
import br.com.ume.application.features.brcode.inspectBrcode.useCase.dtos.InspectedBrcodeDto
import br.com.ume.application.features.payments.createPayment.useCase.dtos.CreateTransactionEvent
import br.com.ume.application.features.payments.createPayment.useCase.enums.ProcessPaymentErrorEnum
import br.com.ume.application.features.payments.createPayment.useCase.validators.validatePayment
import br.com.ume.application.shared.configs.PaymentsTopicConfigurations
import br.com.ume.application.shared.events.EventProvider
import br.com.ume.application.shared.externalServices.coordinator.CoordinatorService
import br.com.ume.application.shared.externalServices.coordinator.dtos.Contract
import br.com.ume.application.shared.externalServices.coordinator.enums.SourceProductEnum
import br.com.ume.libs.logging.gcp.LoggerFactory
import br.com.ume.application.shared.payment.repository.PaymentRepository
import br.com.ume.application.shared.payment.repository.dtos.PaymentDto
import br.com.ume.application.shared.payment.repository.dtos.PaymentOriginDto
import br.com.ume.application.shared.utils.utcNow
import io.micronaut.runtime.http.scope.RequestScope
import java.util.logging.Logger
import javax.transaction.Transactional

@RequestScope
class CreatePaymentUseCaseImpl(
    private val inspectBrcodeGateway: InspectBrcodeGatewayImpl,
    private val coordinatorService: CoordinatorService,
    private val paymentRepository: PaymentRepository,
    private val eventProvider: EventProvider,
    private val paymentsTopicConfigurations: PaymentsTopicConfigurations,
) : CreatePaymentUseCase {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(CreatePaymentUseCaseImpl::class.java.name)
    }

    @Transactional
    override fun execute(
        proposalId: String,
        contractId: String,
        brCodeString: String,
        userId: String,
        headers: Map<String, String>
    ): PaymentDto {
        val payment = paymentRepository.findPayment(brCodeString, contractId)
        if (payment != null) return payment

        val brCode: InspectedBrcodeDto = this.inspectBrcodeGateway.inspect(brCodeString, userId).let {
            it.error?.let { error -> throw BusinessRuleException(error.toString()) }
            it.value!!
        }
        val contract: Contract = coordinatorService.getContract(contractId, headers)
            ?: throw NotFoundException(ProcessPaymentErrorEnum.CONTRACT_NOT_FOUND.toString())

        validatePayment(proposalId, contract, brCode)

        return createPayment(proposalId, brCodeString, brCode.value, contract, headers)
    }

    private fun createPayment(
        proposalId: String,
        brCode: String,
        value: Double,
        contract: Contract,
        headers: Map<String, String>,
    ): PaymentDto {
        val utcNow = utcNow()
        val payment = paymentRepository.createPayment(
            PaymentDto(
                paymentOrigin = PaymentOriginDto(
                    userId = contract.borrowerId,
                    contractId = contract.id,
                    creationTimestamp = utcNow,
                    updateTimestamp = utcNow,
                ),
                value = value,
                brCode = brCode,
                creationTimestamp = utcNow,
                updateTimestamp = utcNow,
            )
        )
        coordinatorService.acceptProposal(proposalId, headers)
            ?: throw InternalErrorException(ProcessPaymentErrorEnum.ERROR_ACCEPTING_PROPOSAL.toString())
        emitTransactionEvent(contract.borrowerId, payment.id.toString(), brCode)
        return payment
    }

    private fun emitTransactionEvent(userId: String, paymentId: String, brCode: String) {
        val eventData = CreateTransactionEvent(
            brCode,
            userId,
            sourceProductReferenceId = paymentId,
            sourceProductReferenceName = SourceProductEnum.PAY_ANYWHERE.toString()
        )
        eventProvider.publish(
            paymentsTopicConfigurations.projectId!!,
            paymentsTopicConfigurations.topicId!!,
            eventData,
            null
        )
    }
}