package br.com.ume.application.transaction.handleRefund.useCase

import br.com.ume.api.configs.RefundConfigurations
import br.com.ume.api.configs.RefundEventConfigurations
import br.com.ume.api.controllers.pubSub.testHelpers.buildRefundPaymentEvent
import br.com.ume.api.exceptions.types.InternalErrorException
import br.com.ume.application.features.transaction.handleRefund.dtos.RefundEvent
import br.com.ume.application.features.transaction.handleRefund.enums.HandleRefundErrorEnum
import br.com.ume.application.features.transaction.handleRefund.enums.RefundTypeEnum
import br.com.ume.application.features.transaction.handleRefund.useCase.HandleRefundUseCase
import br.com.ume.application.features.transaction.handleRefund.useCase.HandleRefundUseCaseImpl
import br.com.ume.application.shared.events.EventProvider
import br.com.ume.application.shared.transaction.enums.TransactionStatusEnum
import br.com.ume.application.shared.transaction.gateway.TransactionGateway
import br.com.ume.application.shared.transaction.testBuilders.TransactionBuilder
import br.com.ume.application.shared.transaction.testBuilders.buildTransactionRefund
import br.com.ume.application.shared.utils.bankingPartner.parseValueToPartnerAmount
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing

class HandleRefundUseCaseTests {
    private lateinit var transactionGatewayMock: TransactionGateway
    private lateinit var refundConfigurationsMock: RefundConfigurations
    private lateinit var eventProviderMock: EventProvider
    private lateinit var refundEventConfigurationsMock: RefundEventConfigurations
    private lateinit var handleRefundUseCase: HandleRefundUseCase

    companion object {
        private const val defaultRefundEventProjectId = "project-test"
        private const val defaultRefundEventTopicId = "topic-test"
        private val defaultTransaction = TransactionBuilder.build(status = TransactionStatusEnum.SUCCESS)
        private const val defaultPaymentLeftoverCutoffValue = 1.5
    }

    @BeforeEach
    fun setUp() {
        transactionGatewayMock = Mockito.mock(TransactionGateway::class.java)
        refundConfigurationsMock = Mockito.mock(RefundConfigurations::class.java)
        Mockito.`when`(refundConfigurationsMock.paymentLeftoverCutoffValue).thenReturn(defaultPaymentLeftoverCutoffValue)
        eventProviderMock = Mockito.mock(EventProvider::class.java)
        refundEventConfigurationsMock = Mockito.mock(RefundEventConfigurations::class.java)
        Mockito.`when`(refundEventConfigurationsMock.projectId).thenReturn(defaultRefundEventProjectId)
        Mockito.`when`(refundEventConfigurationsMock.topicId).thenReturn(defaultRefundEventTopicId)

        handleRefundUseCase = HandleRefundUseCaseImpl(
            transactionGatewayMock,
            refundConfigurationsMock,
            eventProviderMock,
            refundEventConfigurationsMock
        )
    }

    @Nested
    @DisplayName("TOTAL transaction refund")
    inner class TotalTransactionRefund {
        private val defaultOriginalValue = defaultTransaction.value
        private val defaultPreviousValue = defaultTransaction.value
        private val defaultCurrentValue = 0.0
        private val defaultEvent = buildRefundPaymentEvent(amount = 0)
        private val defaultEventPayment = defaultEvent.log.payment
        private val defaultTransactionRefund = buildTransactionRefund(
            typeEnum = RefundTypeEnum.TOTAL,
            originalValue = defaultOriginalValue,
            previousValue = defaultPreviousValue,
            currentValue = defaultCurrentValue
        )
        private val defaultTransactionWithRefund = defaultTransaction.copy(
            refunds = defaultTransaction.refunds + defaultTransactionRefund
        )
        private val defaultUpdatedTransaction = defaultTransactionWithRefund.copy(
            value = defaultCurrentValue,
            status = TransactionStatusEnum.REFUNDED
        )
        private val defaultRefundEvent = RefundEvent(defaultUpdatedTransaction)
        private val defaultRefundEventAttributes = hashMapOf("type" to RefundTypeEnum.TOTAL.toString())

        @Test
        fun `Should handle a refund`() {
            // Given
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultEventPayment.id))
                .thenReturn(defaultTransaction)
            // TODO: Mock the actual value instead of using any()
            Mockito.`when`(transactionGatewayMock.createTransactionRefund(any()))
                .thenReturn(defaultTransactionRefund)
            Mockito.`when`(transactionGatewayMock.updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )).thenReturn(defaultUpdatedTransaction)
            doNothing().`when`(eventProviderMock).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )

            // When
            handleRefundUseCase.execute(defaultEvent)

            // Then
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(defaultEventPayment.id)
            Mockito.verify(transactionGatewayMock, times(1)).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, times(1)).updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )
            Mockito.verify(transactionGatewayMock, never()).updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )
            Mockito.verify(eventProviderMock, times(1)).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )
        }

        @Test
        fun `Should throw if transaction not found`() {
            // Given
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultEventPayment.id))
                .thenReturn(null)

            // When
            val exception = assertThrows<InternalErrorException> {
                handleRefundUseCase.execute(defaultEvent)
            }

            // Then
            assertEquals(HandleRefundErrorEnum.TRANSACTION_NOT_FOUND.toString(), exception.message)
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(defaultEventPayment.id)
            Mockito.verify(transactionGatewayMock, never()).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, never()).updateTransactionAsRefunded(any(), any(), any())
            Mockito.verify(transactionGatewayMock, never()).updateTransactionForRefund(any(), any())
            Mockito.verify(eventProviderMock, never()).publish(any(), any(), any(), any())
        }

        @Test
        fun `Should throw if get transaction fails`() {
            // Given
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultEventPayment.id))
                .thenAnswer { throw Exception("ERROR") }

            // When
            val exception = assertThrows<Exception> {
                handleRefundUseCase.execute(defaultEvent)
            }

            // Then
            assertEquals("ERROR", exception.message)
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(defaultEventPayment.id)
            Mockito.verify(transactionGatewayMock, never()).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, never()).updateTransactionAsRefunded(any(), any(), any())
            Mockito.verify(transactionGatewayMock, never()).updateTransactionForRefund(any(), any())
            Mockito.verify(eventProviderMock, never()).publish(any(), any(), any(), any())
        }

        @Test
        fun `Should return if event is duplicated`() {
            // Given
            val transaction = TransactionBuilder.build(status = TransactionStatusEnum.REFUNDED)
            val amountEqualsToTransactionValue = parseValueToPartnerAmount(transaction.value)
            val eventPayment = buildRefundPaymentEvent(amount = amountEqualsToTransactionValue)
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(eventPayment.log.payment.id))
                .thenReturn(transaction)

            // When
            handleRefundUseCase.execute(eventPayment)

            // Then
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(eventPayment.log.payment.id)
            Mockito.verify(transactionGatewayMock, never()).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, never()).updateTransactionAsRefunded(any(), any(), any())
            Mockito.verify(transactionGatewayMock, never()).updateTransactionForRefund(any(), any())
            Mockito.verify(eventProviderMock, never()).publish(any(), any(), any(), any())
        }

        @Test
        fun `Should return if event is out of order`() {
            // Given
            val transaction = TransactionBuilder.build(status = TransactionStatusEnum.REFUNDED)
            val amountGreaterThanTransactionValue = parseValueToPartnerAmount(transaction.value) + 1
            val eventPayment = buildRefundPaymentEvent(amount = amountGreaterThanTransactionValue)
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(eventPayment.log.payment.id))
                .thenReturn(transaction)

            // When
            handleRefundUseCase.execute(eventPayment)

            // Then
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(eventPayment.log.payment.id)
            Mockito.verify(transactionGatewayMock, never()).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, never()).updateTransactionAsRefunded(any(), any(), any())
            Mockito.verify(transactionGatewayMock, never()).updateTransactionForRefund(any(), any())
            Mockito.verify(eventProviderMock, never()).publish(any(), any(), any(), any())
        }

        @Test
        fun `Should throw if transaction refund is not created`() {
            // Given
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultEventPayment.id))
                .thenReturn(defaultTransaction)
            Mockito.`when`(transactionGatewayMock.createTransactionRefund(any()))
                .thenReturn(null)

            // When
            val exception = assertThrows<InternalErrorException> {
                handleRefundUseCase.execute(defaultEvent)
            }

            // Then
            assertEquals(HandleRefundErrorEnum.CREATE_TRANSACTION_REFUND_ERROR.toString(), exception.message)
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(defaultEventPayment.id)
            Mockito.verify(transactionGatewayMock, times(1)).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, never()).updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )
            Mockito.verify(transactionGatewayMock, never()).updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )
            Mockito.verify(eventProviderMock, never()).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )
        }

        @Test
        fun `Should throw if transaction refund creation fails`() {
            // Given
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultEventPayment.id))
                .thenReturn(defaultTransaction)
            Mockito.`when`(transactionGatewayMock.createTransactionRefund(any()))
                .thenAnswer { throw Exception("ERROR") }

            // When
            val exception = assertThrows<Exception> {
                handleRefundUseCase.execute(defaultEvent)
            }

            // Then
            assertEquals("ERROR", exception.message)
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(defaultEventPayment.id)
            Mockito.verify(transactionGatewayMock, times(1)).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, never()).updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )
            Mockito.verify(transactionGatewayMock, never()).updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )
            Mockito.verify(eventProviderMock, never()).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )
        }

        @Test
        fun `Should throw if transaction is not updated`() {
            // Given
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultEventPayment.id))
                .thenReturn(defaultTransaction)
            Mockito.`when`(transactionGatewayMock.createTransactionRefund(any())).thenReturn(defaultTransactionRefund)
            Mockito.`when`(transactionGatewayMock.updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )).thenReturn(null)

            // When
            val exception = assertThrows<InternalErrorException> {
                handleRefundUseCase.execute(defaultEvent)
            }

            // Then
            assertEquals(HandleRefundErrorEnum.UPDATE_TRANSACTION_ERROR.toString(), exception.message)
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(defaultEventPayment.id)
            Mockito.verify(transactionGatewayMock, times(1)).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, times(1)).updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )
            Mockito.verify(transactionGatewayMock, never()).updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )
            Mockito.verify(eventProviderMock, never()).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )
        }

        @Test
        fun `Should throw if transaction update fails`() {
            // Given
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultEventPayment.id))
                .thenReturn(defaultTransaction)
            Mockito.`when`(transactionGatewayMock.createTransactionRefund(any())).thenReturn(defaultTransactionRefund)
            Mockito.`when`(transactionGatewayMock.updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )).thenAnswer { throw Exception("ERROR") }

            // When
            val exception = assertThrows<Exception> {
                handleRefundUseCase.execute(defaultEvent)
            }

            // Then
            assertEquals("ERROR", exception.message)
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(defaultEventPayment.id)
            Mockito.verify(transactionGatewayMock, times(1)).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, times(1)).updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )
            Mockito.verify(transactionGatewayMock, never()).updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )
            Mockito.verify(eventProviderMock, never()).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )
        }
    }

    @Nested
    @DisplayName("TOTAL_WITH_LEFTOVER transaction refund")
    inner class TotalWithLeftoverTransactionRefund {
        private val defaultOriginalValue = defaultTransaction.value
        private val defaultPreviousValue = defaultTransaction.value
        private val defaultCurrentValue = defaultPaymentLeftoverCutoffValue
        private val defaultEvent = buildRefundPaymentEvent(
            amount = parseValueToPartnerAmount(defaultPaymentLeftoverCutoffValue)
        )
        private val defaultEventPayment = defaultEvent.log.payment
        private val defaultTransactionRefund = buildTransactionRefund(
            typeEnum = RefundTypeEnum.TOTAL_WITH_LEFTOVER,
            originalValue = defaultOriginalValue,
            previousValue = defaultPreviousValue,
            currentValue = defaultCurrentValue
        )
        private val defaultTransactionWithRefund = defaultTransaction.copy(
            refunds = defaultTransaction.refunds + defaultTransactionRefund
        )
        private val defaultUpdatedTransaction = defaultTransactionWithRefund.copy(
            value = defaultCurrentValue,
            status = TransactionStatusEnum.REFUNDED
        )
        private val defaultRefundEvent = RefundEvent(defaultUpdatedTransaction)
        private val defaultRefundEventAttributes = hashMapOf("type" to RefundTypeEnum.TOTAL_WITH_LEFTOVER.toString())

        @Test
        fun `Should handle a refund`() {
            // Given
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultEventPayment.id))
                .thenReturn(defaultTransaction)
            Mockito.`when`(transactionGatewayMock.createTransactionRefund(any()))
                .thenReturn(defaultTransactionRefund)
            Mockito.`when`(transactionGatewayMock.updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )).thenReturn(defaultUpdatedTransaction)
            doNothing().`when`(eventProviderMock).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )

            // When
            handleRefundUseCase.execute(defaultEvent)

            // Then
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(defaultEventPayment.id)
            Mockito.verify(transactionGatewayMock, times(1)).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, times(1)).updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )
            Mockito.verify(transactionGatewayMock, never()).updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )
            Mockito.verify(eventProviderMock, times(1)).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )
        }

        @Test
        fun `Should throw if transaction refund is not created`() {
            // Given
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultEventPayment.id))
                .thenReturn(defaultTransaction)
            Mockito.`when`(transactionGatewayMock.createTransactionRefund(any()))
                .thenReturn(null)

            // When
            val exception = assertThrows<InternalErrorException> {
                handleRefundUseCase.execute(defaultEvent)
            }

            // Then
            assertEquals(HandleRefundErrorEnum.CREATE_TRANSACTION_REFUND_ERROR.toString(), exception.message)
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(defaultEventPayment.id)
            Mockito.verify(transactionGatewayMock, times(1)).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, never()).updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )
            Mockito.verify(transactionGatewayMock, never()).updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )
            Mockito.verify(eventProviderMock, never()).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )
        }

        @Test
        fun `Should throw if transaction refund creation fails`() {
            // Given
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultEventPayment.id))
                .thenReturn(defaultTransaction)
            Mockito.`when`(transactionGatewayMock.createTransactionRefund(any()))
                .thenAnswer { throw Exception("ERROR") }

            // When
            val exception = assertThrows<Exception> {
                handleRefundUseCase.execute(defaultEvent)
            }

            // Then
            assertEquals("ERROR", exception.message)
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(defaultEventPayment.id)
            Mockito.verify(transactionGatewayMock, times(1)).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, never()).updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )
            Mockito.verify(transactionGatewayMock, never()).updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )
            Mockito.verify(eventProviderMock, never()).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )
        }

        @Test
        fun `Should throw if transaction is not updated`() {
            // Given
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultEventPayment.id))
                .thenReturn(defaultTransaction)
            Mockito.`when`(transactionGatewayMock.createTransactionRefund(any())).thenReturn(defaultTransactionRefund)
            Mockito.`when`(transactionGatewayMock.updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )).thenReturn(null)

            // When
            val exception = assertThrows<InternalErrorException> {
                handleRefundUseCase.execute(defaultEvent)
            }

            // Then
            assertEquals(HandleRefundErrorEnum.UPDATE_TRANSACTION_ERROR.toString(), exception.message)
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(defaultEventPayment.id)
            Mockito.verify(transactionGatewayMock, times(1)).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, times(1)).updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )
            Mockito.verify(transactionGatewayMock, never()).updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )
            Mockito.verify(eventProviderMock, never()).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )
        }

        @Test
        fun `Should throw if transaction update fails`() {
            // Given
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultEventPayment.id))
                .thenReturn(defaultTransaction)
            Mockito.`when`(transactionGatewayMock.createTransactionRefund(any())).thenReturn(defaultTransactionRefund)
            Mockito.`when`(transactionGatewayMock.updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )).thenAnswer { throw Exception("ERROR") }

            // When
            val exception = assertThrows<Exception> {
                handleRefundUseCase.execute(defaultEvent)
            }

            // Then
            assertEquals("ERROR", exception.message)
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(defaultEventPayment.id)
            Mockito.verify(transactionGatewayMock, times(1)).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, times(1)).updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )
            Mockito.verify(transactionGatewayMock, never()).updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )
            Mockito.verify(eventProviderMock, never()).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )
        }
    }

    @Nested
    @DisplayName("PARTIAL transaction refund")
    inner class PartialTransactionRefund {
        private val defaultOriginalValue = defaultTransaction.value
        private val defaultPreviousValue = defaultTransaction.value
        private val defaultCurrentValue = defaultPaymentLeftoverCutoffValue + 1
        private val defaultEvent = buildRefundPaymentEvent(
            amount = parseValueToPartnerAmount(defaultCurrentValue)
        )
        private val defaultEventPayment = defaultEvent.log.payment
        private val defaultTransactionRefund = buildTransactionRefund(
            typeEnum = RefundTypeEnum.PARTIAL,
            originalValue = defaultOriginalValue,
            previousValue = defaultPreviousValue,
            currentValue = defaultCurrentValue
        )
        private val defaultTransactionWithRefund = defaultTransaction.copy(
            refunds = defaultTransaction.refunds + defaultTransactionRefund
        )
        private val defaultUpdatedTransaction = defaultTransactionWithRefund.copy(value = defaultCurrentValue)
        private val defaultRefundEvent = RefundEvent(defaultUpdatedTransaction)
        private val defaultRefundEventAttributes = hashMapOf("type" to RefundTypeEnum.PARTIAL.toString())

        @Test
        fun `Should handle a refund`() {
            // Given
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultEventPayment.id))
                .thenReturn(defaultTransaction)
            Mockito.`when`(transactionGatewayMock.createTransactionRefund(any()))
                .thenReturn(defaultTransactionRefund)
            Mockito.`when`(transactionGatewayMock.updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )).thenReturn(defaultUpdatedTransaction)
            doNothing().`when`(eventProviderMock).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )

            // When
            handleRefundUseCase.execute(defaultEvent)

            // Then
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(defaultEventPayment.id)
            Mockito.verify(transactionGatewayMock, times(1)).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, never()).updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )
            Mockito.verify(transactionGatewayMock, times(1)).updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )
            Mockito.verify(eventProviderMock, times(1)).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )
        }

        @Test
        fun `Should throw if transaction refund is not created`() {
            // Given
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultEventPayment.id))
                .thenReturn(defaultTransaction)
            Mockito.`when`(transactionGatewayMock.createTransactionRefund(any()))
                .thenReturn(null)

            // When
            val exception = assertThrows<InternalErrorException> {
                handleRefundUseCase.execute(defaultEvent)
            }

            // Then
            assertEquals(HandleRefundErrorEnum.CREATE_TRANSACTION_REFUND_ERROR.toString(), exception.message)
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(defaultEventPayment.id)
            Mockito.verify(transactionGatewayMock, times(1)).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, never()).updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )
            Mockito.verify(transactionGatewayMock, never()).updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )
            Mockito.verify(eventProviderMock, never()).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )
        }

        @Test
        fun `Should throw if transaction refund creation fails`() {
            // Given
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultEventPayment.id))
                .thenReturn(defaultTransaction)
            Mockito.`when`(transactionGatewayMock.createTransactionRefund(any()))
                .thenAnswer { throw Exception("ERROR") }

            // When
            val exception = assertThrows<Exception> {
                handleRefundUseCase.execute(defaultEvent)
            }

            // Then
            assertEquals("ERROR", exception.message)
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(defaultEventPayment.id)
            Mockito.verify(transactionGatewayMock, times(1)).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, never()).updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )
            Mockito.verify(transactionGatewayMock, never()).updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )
            Mockito.verify(eventProviderMock, never()).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )
        }

        @Test
        fun `Should throw if transaction is not updated`() {
            // Given
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultEventPayment.id))
                .thenReturn(defaultTransaction)
            Mockito.`when`(transactionGatewayMock.createTransactionRefund(any())).thenReturn(defaultTransactionRefund)
            Mockito.`when`(transactionGatewayMock.updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )).thenReturn(null)

            // When
            val exception = assertThrows<InternalErrorException> {
                handleRefundUseCase.execute(defaultEvent)
            }

            // Then
            assertEquals(HandleRefundErrorEnum.UPDATE_TRANSACTION_ERROR.toString(), exception.message)
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(defaultEventPayment.id)
            Mockito.verify(transactionGatewayMock, times(1)).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, never()).updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )
            Mockito.verify(transactionGatewayMock, times(1)).updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )
            Mockito.verify(eventProviderMock, never()).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )
        }

        @Test
        fun `Should throw if transaction update fails`() {
            // Given
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultEventPayment.id))
                .thenReturn(defaultTransaction)
            Mockito.`when`(transactionGatewayMock.createTransactionRefund(any())).thenReturn(defaultTransactionRefund)
            Mockito.`when`(transactionGatewayMock.updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )).thenAnswer { throw Exception("ERROR") }

            // When
            val exception = assertThrows<Exception> {
                handleRefundUseCase.execute(defaultEvent)
            }

            // Then
            assertEquals("ERROR", exception.message)
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(defaultEventPayment.id)
            Mockito.verify(transactionGatewayMock, times(1)).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, never()).updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )
            Mockito.verify(transactionGatewayMock, times(1)).updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )
            Mockito.verify(eventProviderMock, never()).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )
        }
    }

    @Nested
    @DisplayName("Multiple PARTIAL transaction refunds")
    inner class MultiplePartialTransactionRefunds {
        private val originalTransaction = TransactionBuilder.build(status = TransactionStatusEnum.SUCCESS)
        private val previousTransactionRefund = buildTransactionRefund(
            typeEnum = RefundTypeEnum.PARTIAL,
            originalValue = originalTransaction.value,
            previousValue = originalTransaction.value,
            currentValue = originalTransaction.value - 1
        )
        private val defaultTransaction = originalTransaction.copy(refunds = listOf(previousTransactionRefund))
        private val defaultOriginalValue = defaultTransaction.value
        private val defaultPreviousValue = previousTransactionRefund.currentValue
        private val defaultCurrentValue = defaultPaymentLeftoverCutoffValue + 1
        private val defaultEvent = buildRefundPaymentEvent(
            amount = parseValueToPartnerAmount(defaultCurrentValue)
        )
        private val defaultEventPayment = defaultEvent.log.payment
        private val defaultTransactionRefund = buildTransactionRefund(
            typeEnum = RefundTypeEnum.PARTIAL,
            originalValue = defaultOriginalValue,
            previousValue = defaultPreviousValue,
            currentValue = defaultCurrentValue
        )
        private val defaultTransactionWithRefund = defaultTransaction.copy(
            refunds = defaultTransaction.refunds + defaultTransactionRefund
        )
        private val defaultUpdatedTransaction = defaultTransactionWithRefund.copy(value = defaultCurrentValue)
        private val defaultRefundEvent = RefundEvent(defaultUpdatedTransaction)
        private val defaultRefundEventAttributes = hashMapOf("type" to RefundTypeEnum.PARTIAL.toString())

        @Test
        fun `Should handle a refund`() {
            // Given
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultEventPayment.id))
                .thenReturn(defaultTransaction)
            Mockito.`when`(transactionGatewayMock.createTransactionRefund(any()))
                .thenReturn(defaultTransactionRefund)
            Mockito.`when`(transactionGatewayMock.updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )).thenReturn(defaultUpdatedTransaction)
            doNothing().`when`(eventProviderMock).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )

            // When
            handleRefundUseCase.execute(defaultEvent)

            // Then
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(defaultEventPayment.id)
            Mockito.verify(transactionGatewayMock, times(1)).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, never()).updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )
            Mockito.verify(transactionGatewayMock, times(1)).updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )
            Mockito.verify(eventProviderMock, times(1)).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )
        }

        @Test
        fun `Should throw if transaction refund is not created`() {
            // Given
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultEventPayment.id))
                .thenReturn(defaultTransaction)
            Mockito.`when`(transactionGatewayMock.createTransactionRefund(any()))
                .thenReturn(null)

            // When
            val exception = assertThrows<InternalErrorException> {
                handleRefundUseCase.execute(defaultEvent)
            }

            // Then
            assertEquals(HandleRefundErrorEnum.CREATE_TRANSACTION_REFUND_ERROR.toString(), exception.message)
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(defaultEventPayment.id)
            Mockito.verify(transactionGatewayMock, times(1)).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, never()).updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )
            Mockito.verify(transactionGatewayMock, never()).updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )
            Mockito.verify(eventProviderMock, never()).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )
        }

        @Test
        fun `Should throw if transaction refund creation fails`() {
            // Given
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultEventPayment.id))
                .thenReturn(defaultTransaction)
            Mockito.`when`(transactionGatewayMock.createTransactionRefund(any()))
                .thenAnswer { throw Exception("ERROR") }

            // When
            val exception = assertThrows<Exception> {
                handleRefundUseCase.execute(defaultEvent)
            }

            // Then
            assertEquals("ERROR", exception.message)
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(defaultEventPayment.id)
            Mockito.verify(transactionGatewayMock, times(1)).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, never()).updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )
            Mockito.verify(transactionGatewayMock, never()).updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )
            Mockito.verify(eventProviderMock, never()).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )
        }

        @Test
        fun `Should throw if transaction is not updated`() {
            // Given
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultEventPayment.id))
                .thenReturn(defaultTransaction)
            Mockito.`when`(transactionGatewayMock.createTransactionRefund(any())).thenReturn(defaultTransactionRefund)
            Mockito.`when`(transactionGatewayMock.updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )).thenReturn(null)

            // When
            val exception = assertThrows<InternalErrorException> {
                handleRefundUseCase.execute(defaultEvent)
            }

            // Then
            assertEquals(HandleRefundErrorEnum.UPDATE_TRANSACTION_ERROR.toString(), exception.message)
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(defaultEventPayment.id)
            Mockito.verify(transactionGatewayMock, times(1)).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, never()).updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )
            Mockito.verify(transactionGatewayMock, times(1)).updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )
            Mockito.verify(eventProviderMock, never()).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )
        }

        @Test
        fun `Should throw if transaction update fails`() {
            // Given
            Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultEventPayment.id))
                .thenReturn(defaultTransaction)
            Mockito.`when`(transactionGatewayMock.createTransactionRefund(any())).thenReturn(defaultTransactionRefund)
            Mockito.`when`(transactionGatewayMock.updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )).thenAnswer { throw Exception("ERROR") }

            // When
            val exception = assertThrows<Exception> {
                handleRefundUseCase.execute(defaultEvent)
            }

            // Then
            assertEquals("ERROR", exception.message)
            Mockito.verify(transactionGatewayMock, times(1)).getTransactionByPartnerExternalId(defaultEventPayment.id)
            Mockito.verify(transactionGatewayMock, times(1)).createTransactionRefund(any())
            Mockito.verify(transactionGatewayMock, never()).updateTransactionAsRefunded(
                defaultTransactionWithRefund, defaultCurrentValue, TransactionStatusEnum.REFUNDED
            )
            Mockito.verify(transactionGatewayMock, times(1)).updateTransactionForRefund(
                defaultTransactionWithRefund, defaultCurrentValue
            )
            Mockito.verify(eventProviderMock, never()).publish(
                defaultRefundEventProjectId,
                defaultRefundEventTopicId,
                defaultRefundEvent,
                defaultRefundEventAttributes
            )
        }
    }
}