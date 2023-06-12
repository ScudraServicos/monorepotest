package br.com.ume.application.payments.createPayment

import br.com.ume.api.exceptions.types.BusinessRuleException
import br.com.ume.api.exceptions.types.InternalErrorException
import br.com.ume.api.exceptions.types.NotFoundException
import br.com.ume.application.features.brcode.inspectBrcode.gateway.inspectBrcode.InspectBrcodeGatewayImpl
import br.com.ume.application.features.brcode.inspectBrcode.gateway.inspectBrcode.dtos.InspectBrcodeGatewayOutput
import br.com.ume.application.features.brcode.inspectBrcode.useCase.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.payments.createPayment.useCase.CreatePaymentUseCase
import br.com.ume.application.features.payments.createPayment.useCase.CreatePaymentUseCaseImpl
import br.com.ume.application.features.payments.createPayment.useCase.dtos.CreateTransactionEvent
import br.com.ume.application.shared.accessControl.domain.AccessControl
import br.com.ume.application.shared.configs.PaymentsTopicConfigurations
import br.com.ume.application.shared.events.EventProvider
import br.com.ume.application.shared.externalServices.coordinator.CoordinatorService
import br.com.ume.application.shared.externalServices.coordinator.enums.SourceProductEnum
import br.com.ume.application.shared.payment.repository.PaymentRepository
import br.com.ume.application.shared.payment.repository.dtos.PaymentDto
import br.com.ume.application.shared.payment.repository.dtos.PaymentOriginDto
import br.com.ume.application.shared.testBuilders.ContractBuilder
import br.com.ume.application.shared.testBuilders.InspectedBrcodeBuilder
import br.com.ume.application.shared.utils.utcNow
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mockito
import org.mockito.kotlin.any
import java.util.*

class CreatePaymentUseCaseTests {

    companion object {
        private const val defaultContractId = "123"
        private const val defaultProposalId = "2"
        private const val defaultBorrowerId = "1"
        private const val defaultToken = "user.jwt.token"
        private const val defaultXForwardedFor = "192.168.0.1,192.168.0.2"
        private val defaultHeaders = mapOf("Authorization" to defaultToken, "X-Forwarded-For" to defaultXForwardedFor)
        private const val defaultBrCode = "brCode"
        private val defaultAccessControl = AccessControl(defaultBorrowerId, emptySet(), true)
        private val defaultContract = ContractBuilder.buildContract(
            defaultContractId,
            defaultBorrowerId,
            listOf(ContractBuilder.buildProposal(defaultProposalId, defaultContractId))
        )
        private val defaultInspectedBrcode = InspectedBrcodeBuilder.buildStatic(value = 10.0)
        private val utcNow = utcNow()
        private val defaultPayment = PaymentDto(
            id = UUID.randomUUID(),
            externalId = 1,
            paymentOrigin = PaymentOriginDto(
                id = UUID.randomUUID(),
                externalId = 1,
                userId = defaultContract.borrowerId,
                contractId = defaultContract.id,
                creationTimestamp = utcNow,
                updateTimestamp = utcNow,
            ),
            value = 10.0,
            brCode = defaultBrCode,
            creationTimestamp = utcNow,
            updateTimestamp = utcNow,
        )
        private val defaultEvent = CreateTransactionEvent(
            brCode = defaultBrCode,
            userId = defaultBorrowerId,
            sourceProductReferenceId = defaultPayment.id.toString(),
            sourceProductReferenceName = SourceProductEnum.PAY_ANYWHERE.toString()
        )
    }

    private lateinit var inspectBrcodeGatewayMock: InspectBrcodeGatewayImpl
    private lateinit var coordinatorServiceMock: CoordinatorService
    private lateinit var paymentRepositoryMock: PaymentRepository
    private lateinit var eventProviderMock: EventProvider
    private lateinit var paymentsTopicConfigurationsMock: PaymentsTopicConfigurations
    private lateinit var createPaymentUseCase: CreatePaymentUseCase

    @BeforeEach
    fun setUp() {
        inspectBrcodeGatewayMock = Mockito.mock(InspectBrcodeGatewayImpl::class.java)
        coordinatorServiceMock = Mockito.mock(CoordinatorService::class.java)
        paymentRepositoryMock = Mockito.mock(PaymentRepository::class.java)
        eventProviderMock = Mockito.mock(EventProvider::class.java)
        paymentsTopicConfigurationsMock = Mockito.mock(PaymentsTopicConfigurations::class.java)
        Mockito.`when`(paymentsTopicConfigurationsMock.projectId).thenReturn("project-test")
        Mockito.`when`(paymentsTopicConfigurationsMock.topicId).thenReturn("topic-test")
        createPaymentUseCase = CreatePaymentUseCaseImpl(
            inspectBrcodeGatewayMock,
            coordinatorServiceMock,
            paymentRepositoryMock,
            eventProviderMock,
            paymentsTopicConfigurationsMock,
        )
    }

    @Nested
    @DisplayName("Execute()")
    inner class Execute {

        @Nested
        @DisplayName("When payment does not exist")
        inner class WhenPaymentDoesNotExist {

            @Test
            fun `Should create a payment successfully`() {
                Mockito.`when`(paymentRepositoryMock.findPayment(defaultBrCode, defaultContractId)).thenReturn(null)
                Mockito.`when`(inspectBrcodeGatewayMock.inspect(defaultBrCode, defaultBorrowerId)).thenReturn(
                    InspectBrcodeGatewayOutput(defaultInspectedBrcode)
                )
                Mockito.`when`(coordinatorServiceMock.getContract(defaultContractId, defaultHeaders))
                    .thenReturn(defaultContract)
                Mockito.`when`(paymentRepositoryMock.createPayment(any())).thenReturn(defaultPayment)
                val acceptedContract = ContractBuilder.buildContract(
                    defaultContractId,
                    defaultBorrowerId,
                    listOf(ContractBuilder.buildProposal(defaultProposalId, defaultContractId, wasAccepted = true))
                )
                Mockito.`when`(coordinatorServiceMock.acceptProposal(defaultProposalId, defaultHeaders))
                    .thenReturn(acceptedContract)
                Mockito.doNothing().`when`(eventProviderMock).publish("project-test", "topic-test", defaultEvent, null)

                val result = createPaymentUseCase.execute(
                    defaultProposalId, defaultContractId, defaultBrCode, defaultBorrowerId, defaultHeaders
                )
                assertEquals(defaultPayment, result)

                Mockito.verify(paymentRepositoryMock, Mockito.times(1)).findPayment(defaultBrCode, defaultContractId)
                Mockito.verify(inspectBrcodeGatewayMock, Mockito.times(1)).inspect(defaultBrCode, defaultBorrowerId)
                Mockito.verify(coordinatorServiceMock, Mockito.times(1)).getContract(defaultContractId, defaultHeaders)
                Mockito.verify(paymentRepositoryMock, Mockito.times(1)).createPayment(any())
                Mockito.verify(coordinatorServiceMock, Mockito.times(1))
                    .acceptProposal(defaultProposalId, defaultHeaders)
                Mockito.verify(eventProviderMock, Mockito.times(1))
                    .publish("project-test", "topic-test", defaultEvent, null)
            }

            @Test
            fun `Should throw BusinessRuleException if inspect returns an error`() {
                Mockito.`when`(paymentRepositoryMock.findPayment(defaultBrCode, defaultContractId)).thenReturn(null)
                Mockito.`when`(inspectBrcodeGatewayMock.inspect(defaultBrCode, defaultBorrowerId)).thenReturn(
                    InspectBrcodeGatewayOutput(error = InspectBrcodeErrorEnum.ERROR)
                )

                val exception = assertThrows<BusinessRuleException> {
                    createPaymentUseCase.execute(
                        defaultProposalId,
                        defaultContractId,
                        defaultBrCode,
                        defaultBorrowerId,
                        defaultHeaders,
                    )
                }
                assertEquals("ERROR", exception.message)

                Mockito.verify(paymentRepositoryMock, Mockito.times(1)).findPayment(defaultBrCode, defaultContractId)
                Mockito.verify(inspectBrcodeGatewayMock, Mockito.times(1)).inspect(defaultBrCode, defaultBorrowerId)
            }

            @Test
            fun `Should throw NotFoundException if getContract returns null`() {
                Mockito.`when`(paymentRepositoryMock.findPayment(defaultBrCode, defaultContractId)).thenReturn(null)
                Mockito.`when`(inspectBrcodeGatewayMock.inspect(defaultBrCode, defaultBorrowerId)).thenReturn(
                    InspectBrcodeGatewayOutput(defaultInspectedBrcode)
                )
                Mockito.`when`(coordinatorServiceMock.getContract(defaultContractId, defaultHeaders))
                    .thenReturn(null)

                val exception = assertThrows<NotFoundException> {
                    createPaymentUseCase.execute(
                        defaultProposalId,
                        defaultContractId,
                        defaultBrCode,
                        defaultBorrowerId,
                        defaultHeaders,
                    )
                }
                assertEquals("CONTRACT_NOT_FOUND", exception.message)

                Mockito.verify(paymentRepositoryMock, Mockito.times(1)).findPayment(defaultBrCode, defaultContractId)
                Mockito.verify(inspectBrcodeGatewayMock, Mockito.times(1)).inspect(defaultBrCode, defaultBorrowerId)
                Mockito.verify(coordinatorServiceMock, Mockito.times(1)).getContract(defaultContractId, defaultHeaders)
            }

            @Test
            fun `Should throw BusinessRuleException if brCode value is not the same of the contract`() {
                Mockito.`when`(paymentRepositoryMock.findPayment(defaultBrCode, defaultContractId)).thenReturn(null)
                Mockito.`when`(inspectBrcodeGatewayMock.inspect(defaultBrCode, defaultBorrowerId)).thenReturn(
                    InspectBrcodeGatewayOutput(InspectedBrcodeBuilder.buildStatic(value = 10.1))
                )
                Mockito.`when`(coordinatorServiceMock.getContract(defaultContractId, defaultHeaders))
                    .thenReturn(defaultContract)

                val exception = assertThrows<BusinessRuleException> {
                    createPaymentUseCase.execute(
                        defaultProposalId,
                        defaultContractId,
                        defaultBrCode,
                        defaultBorrowerId,
                        defaultHeaders,
                    )
                }
                assertEquals("INVALID_PAYMENT_VALUE", exception.message)

                Mockito.verify(paymentRepositoryMock, Mockito.times(1)).findPayment(defaultBrCode, defaultContractId)
                Mockito.verify(inspectBrcodeGatewayMock, Mockito.times(1)).inspect(defaultBrCode, defaultBorrowerId)
                Mockito.verify(coordinatorServiceMock, Mockito.times(1)).getContract(defaultContractId, defaultHeaders)
            }

            @Test
            fun `Should throw BusinessRuleException if proposal doesn't belong to contract`() {
                // Given
                Mockito.`when`(paymentRepositoryMock.findPayment(defaultBrCode, defaultContractId)).thenReturn(null)
                Mockito.`when`(inspectBrcodeGatewayMock.inspect(defaultBrCode, defaultBorrowerId)).thenReturn(
                    InspectBrcodeGatewayOutput(defaultInspectedBrcode)
                )
                Mockito.`when`(coordinatorServiceMock.getContract(defaultContractId, defaultHeaders))
                    .thenReturn(ContractBuilder.buildContract(
                        defaultContractId,
                        defaultBorrowerId,
                        listOf(ContractBuilder.buildProposal("differentProposalId", defaultContractId))
                    ))

                // When
                val exception = assertThrows<BusinessRuleException> {
                    createPaymentUseCase.execute(
                        defaultProposalId,
                        defaultContractId,
                        defaultBrCode,
                        defaultBorrowerId,
                        defaultHeaders,
                    )
                }

                // Then
                assertEquals("PROPOSAL_DOES_NOT_BELONG_TO_CONTRACT", exception.message)

                Mockito.verify(paymentRepositoryMock, Mockito.times(1)).findPayment(defaultBrCode, defaultContractId)
                Mockito.verify(inspectBrcodeGatewayMock, Mockito.times(1)).inspect(defaultBrCode, defaultBorrowerId)
                Mockito.verify(coordinatorServiceMock, Mockito.times(1)).getContract(defaultContractId, defaultHeaders)
            }

            @Test
            fun `Should throw InternalErrorException if acceptProposal returns null`() {
                Mockito.`when`(paymentRepositoryMock.findPayment(defaultBrCode, defaultContractId)).thenReturn(null)
                Mockito.`when`(inspectBrcodeGatewayMock.inspect(defaultBrCode, defaultBorrowerId)).thenReturn(
                    InspectBrcodeGatewayOutput(defaultInspectedBrcode)
                )
                Mockito.`when`(coordinatorServiceMock.getContract(defaultContractId, defaultHeaders))
                    .thenReturn(defaultContract)
                Mockito.`when`(paymentRepositoryMock.createPayment(any())).thenReturn(defaultPayment)
                Mockito.`when`(coordinatorServiceMock.acceptProposal(defaultProposalId, defaultHeaders))
                    .thenReturn(null)

                val exception = assertThrows<InternalErrorException> {
                    createPaymentUseCase.execute(
                        defaultProposalId,
                        defaultContractId,
                        defaultBrCode,
                        defaultBorrowerId,
                        defaultHeaders,
                    )
                }
                assertEquals("ERROR_ACCEPTING_PROPOSAL", exception.message)

                Mockito.verify(paymentRepositoryMock, Mockito.times(1)).findPayment(defaultBrCode, defaultContractId)
                Mockito.verify(inspectBrcodeGatewayMock, Mockito.times(1)).inspect(defaultBrCode, defaultBorrowerId)
                Mockito.verify(coordinatorServiceMock, Mockito.times(1)).getContract(defaultContractId, defaultHeaders)
                Mockito.verify(paymentRepositoryMock, Mockito.times(1)).createPayment(any())
                Mockito.verify(coordinatorServiceMock, Mockito.times(1))
                    .acceptProposal(defaultProposalId, defaultHeaders)
            }

            @Test
            fun `Should fail if findPayment fails`() {
                Mockito.`when`(paymentRepositoryMock.findPayment(defaultBrCode, defaultContractId)).thenThrow(RuntimeException("ERROR"))

                val exception = assertThrows<RuntimeException> {
                    createPaymentUseCase.execute(
                        defaultProposalId,
                        defaultContractId,
                        defaultBrCode,
                        defaultBorrowerId,
                        defaultHeaders,
                    )
                }
                assertEquals("ERROR", exception.message)

                Mockito.verify(paymentRepositoryMock, Mockito.times(1)).findPayment(defaultBrCode, defaultContractId)
            }

            @Test
            fun `Should fail if inspect fails`() {
                Mockito.`when`(paymentRepositoryMock.findPayment(defaultBrCode, defaultContractId)).thenReturn(null)
                Mockito.`when`(inspectBrcodeGatewayMock.inspect(defaultBrCode, defaultBorrowerId)).thenThrow(RuntimeException("ERROR"))

                val exception = assertThrows<RuntimeException> {
                    createPaymentUseCase.execute(
                        defaultProposalId,
                        defaultContractId,
                        defaultBrCode,
                        defaultBorrowerId,
                        defaultHeaders,
                    )
                }
                assertEquals("ERROR", exception.message)

                Mockito.verify(paymentRepositoryMock, Mockito.times(1)).findPayment(defaultBrCode, defaultContractId)
                Mockito.verify(inspectBrcodeGatewayMock, Mockito.times(1)).inspect(defaultBrCode, defaultBorrowerId)
            }

            @Test
            fun `Should fail if getContract fails`() {
                Mockito.`when`(paymentRepositoryMock.findPayment(defaultBrCode, defaultContractId)).thenReturn(null)
                Mockito.`when`(inspectBrcodeGatewayMock.inspect(defaultBrCode, defaultBorrowerId)).thenReturn(
                    InspectBrcodeGatewayOutput(defaultInspectedBrcode)
                )
                Mockito.`when`(coordinatorServiceMock.getContract(defaultContractId, defaultHeaders))
                    .thenThrow(RuntimeException("ERROR"))

                val exception = assertThrows<RuntimeException> {
                    createPaymentUseCase.execute(
                        defaultProposalId,
                        defaultContractId,
                        defaultBrCode,
                        defaultBorrowerId,
                        defaultHeaders,
                    )
                }
                assertEquals("ERROR", exception.message)

                Mockito.verify(paymentRepositoryMock, Mockito.times(1)).findPayment(defaultBrCode, defaultContractId)
                Mockito.verify(inspectBrcodeGatewayMock, Mockito.times(1)).inspect(defaultBrCode, defaultBorrowerId)
                Mockito.verify(coordinatorServiceMock, Mockito.times(1)).getContract(defaultContractId, defaultHeaders)
            }

            @Test
            fun `Should fail if createPayment fails`() {
                Mockito.`when`(paymentRepositoryMock.findPayment(defaultBrCode, defaultContractId)).thenReturn(null)
                Mockito.`when`(inspectBrcodeGatewayMock.inspect(defaultBrCode, defaultBorrowerId)).thenReturn(
                    InspectBrcodeGatewayOutput(defaultInspectedBrcode)
                )
                Mockito.`when`(coordinatorServiceMock.getContract(defaultContractId, defaultHeaders))
                    .thenReturn(defaultContract)
                Mockito.`when`(paymentRepositoryMock.createPayment(any())).thenThrow(RuntimeException("ERROR"))

                val exception = assertThrows<RuntimeException> {
                    createPaymentUseCase.execute(
                        defaultProposalId,
                        defaultContractId,
                        defaultBrCode,
                        defaultBorrowerId,
                        defaultHeaders,
                    )
                }
                assertEquals("ERROR", exception.message)

                Mockito.verify(paymentRepositoryMock, Mockito.times(1)).findPayment(defaultBrCode, defaultContractId)
                Mockito.verify(inspectBrcodeGatewayMock, Mockito.times(1)).inspect(defaultBrCode, defaultBorrowerId)
                Mockito.verify(coordinatorServiceMock, Mockito.times(1)).getContract(defaultContractId, defaultHeaders)
                Mockito.verify(paymentRepositoryMock, Mockito.times(1)).createPayment(any())
            }

            @Test
            fun `Should fail if acceptProposal fails`() {
                Mockito.`when`(paymentRepositoryMock.findPayment(defaultBrCode, defaultContractId)).thenReturn(null)
                Mockito.`when`(inspectBrcodeGatewayMock.inspect(defaultBrCode, defaultBorrowerId)).thenReturn(
                    InspectBrcodeGatewayOutput(defaultInspectedBrcode)
                )
                Mockito.`when`(coordinatorServiceMock.getContract(defaultContractId, defaultHeaders))
                    .thenReturn(defaultContract)
                Mockito.`when`(paymentRepositoryMock.createPayment(any())).thenReturn(defaultPayment)
                Mockito.`when`(coordinatorServiceMock.acceptProposal(defaultProposalId, defaultHeaders))
                    .thenThrow(RuntimeException("ERROR"))

                val exception = assertThrows<RuntimeException> {
                    createPaymentUseCase.execute(
                        defaultProposalId,
                        defaultContractId,
                        defaultBrCode,
                        defaultBorrowerId,
                        defaultHeaders,
                    )
                }
                assertEquals("ERROR", exception.message)

                Mockito.verify(paymentRepositoryMock, Mockito.times(1)).findPayment(defaultBrCode, defaultContractId)
                Mockito.verify(inspectBrcodeGatewayMock, Mockito.times(1)).inspect(defaultBrCode, defaultBorrowerId)
                Mockito.verify(coordinatorServiceMock, Mockito.times(1)).getContract(defaultContractId, defaultHeaders)
                Mockito.verify(paymentRepositoryMock, Mockito.times(1)).createPayment(any())
                Mockito.verify(coordinatorServiceMock, Mockito.times(1))
                    .acceptProposal(defaultProposalId, defaultHeaders)
            }

            @Test
            fun `Should fail if publish fails`() {
                Mockito.`when`(paymentRepositoryMock.findPayment(defaultBrCode, defaultContractId)).thenReturn(null)
                Mockito.`when`(inspectBrcodeGatewayMock.inspect(defaultBrCode, defaultBorrowerId)).thenReturn(
                    InspectBrcodeGatewayOutput(defaultInspectedBrcode)
                )
                Mockito.`when`(coordinatorServiceMock.getContract(defaultContractId, defaultHeaders))
                    .thenReturn(defaultContract)
                Mockito.`when`(paymentRepositoryMock.createPayment(any())).thenReturn(defaultPayment)
                val acceptedContract = ContractBuilder.buildContract(
                    defaultContractId,
                    defaultBorrowerId,
                    listOf(ContractBuilder.buildProposal(defaultProposalId, defaultContractId, wasAccepted = true))
                )
                Mockito.`when`(coordinatorServiceMock.acceptProposal(defaultProposalId, defaultHeaders))
                    .thenReturn(acceptedContract)
                Mockito.`when`(eventProviderMock.publish("project-test", "topic-test", defaultEvent, null))
                    .thenThrow(RuntimeException("ERROR"))

                val exception = assertThrows<RuntimeException> {
                    createPaymentUseCase.execute(
                        defaultProposalId,
                        defaultContractId,
                        defaultBrCode,
                        defaultBorrowerId,
                        defaultHeaders,
                    )
                }
                assertEquals("ERROR", exception.message)

                Mockito.verify(paymentRepositoryMock, Mockito.times(1)).findPayment(defaultBrCode, defaultContractId)
                Mockito.verify(inspectBrcodeGatewayMock, Mockito.times(1)).inspect(defaultBrCode, defaultBorrowerId)
                Mockito.verify(coordinatorServiceMock, Mockito.times(1)).getContract(defaultContractId, defaultHeaders)
                Mockito.verify(paymentRepositoryMock, Mockito.times(1)).createPayment(any())
                Mockito.verify(coordinatorServiceMock, Mockito.times(1))
                    .acceptProposal(defaultProposalId, defaultHeaders)
                Mockito.verify(eventProviderMock, Mockito.times(1))
                    .publish("project-test", "topic-test", defaultEvent, null)
            }
        }

        @Nested
        @DisplayName("When payment already exist")
        inner class WhenPaymentAlreadyExist {
            @Test
            fun `Should return the existing payment successfully`() {
                Mockito.`when`(paymentRepositoryMock.findPayment(defaultBrCode, defaultContractId)).thenReturn(defaultPayment)

                val result = createPaymentUseCase.execute(
                    defaultProposalId, defaultContractId, defaultBrCode, defaultBorrowerId, defaultHeaders
                )
                assertEquals(defaultPayment, result)

                Mockito.verify(paymentRepositoryMock, Mockito.times(1)).findPayment(defaultBrCode, defaultContractId)
            }
        }
    }
}