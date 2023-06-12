package br.com.ume.application.transaction.updateTransactionStatus

import br.com.ume.api.configs.FeatureFlagsConfigurations
import br.com.ume.api.configs.TransactionFinalizedTopicConfigurations
import br.com.ume.api.exceptions.types.InternalErrorException
import br.com.ume.application.features.transaction.updateTransactionStatus.useCase.UpdateTransactionStatusUseCase
import br.com.ume.application.features.transaction.updateTransactionStatus.useCase.UpdateTransactionStatusUseCaseImpl
import br.com.ume.application.features.transaction.updateTransactionStatus.useCase.dtos.TransactionFinalizedEvent
import br.com.ume.application.features.transaction.updateTransactionStatus.useCase.enums.UpdateTransactionStatusErrorEnum
import br.com.ume.application.shared.events.EventProvider
import br.com.ume.application.shared.transaction.enums.TransactionStatusEnum
import br.com.ume.application.shared.transaction.gateway.TransactionGateway
import br.com.ume.application.shared.transaction.testBuilders.TransactionBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.kotlin.*

class UpdateTransactionStatusUseCaseImplTests {
    private val defaultPartnerExternalId = "123"
    private val defaultPartnerStatus = "created"
    private val defaultTransaction = TransactionBuilder.build()
    private val defaultStatus = TransactionStatusEnum.CREATED
    private val defaultUpdatedTransaction = defaultTransaction.copy(status = defaultStatus)
    private val defaultTransactionFinalizedEvent = TransactionFinalizedEvent(defaultTransaction)
    private val defaultTransactionFinalizedEventAttributes = hashMapOf(
        "status" to defaultStatus.toString(),
        "sourceProductReferenceName" to defaultTransaction.origin.sourceProductReferenceName
    )
    private val defaultEventProjectId = "project-test"
    private val defaultEventTopicId = "topic-test"

    private lateinit var transactionGatewayMock: TransactionGateway
    private lateinit var eventProviderMock: EventProvider
    private lateinit var transactionFinalizedTopicConfigurationsMock: TransactionFinalizedTopicConfigurations
    private lateinit var featureFlagsConfigurationsMock: FeatureFlagsConfigurations
    private lateinit var updateTransactionStatusUseCase: UpdateTransactionStatusUseCase

    @BeforeEach
    fun setUp() {
        transactionGatewayMock = Mockito.mock(TransactionGateway::class.java)
        eventProviderMock = Mockito.mock(EventProvider::class.java)
        transactionFinalizedTopicConfigurationsMock = Mockito.mock(TransactionFinalizedTopicConfigurations::class.java)
        Mockito.`when`(transactionFinalizedTopicConfigurationsMock.projectId).thenReturn(defaultEventProjectId)
        Mockito.`when`(transactionFinalizedTopicConfigurationsMock.topicId).thenReturn(defaultEventTopicId)
        featureFlagsConfigurationsMock = Mockito.mock(FeatureFlagsConfigurations::class.java)
        Mockito.`when`(featureFlagsConfigurationsMock.emitTransactionFinalizedEvent).thenReturn(true)

        updateTransactionStatusUseCase = UpdateTransactionStatusUseCaseImpl(
            transactionGatewayMock,
            eventProviderMock,
            transactionFinalizedTopicConfigurationsMock,
            featureFlagsConfigurationsMock
        )
    }

    @Test
    fun `Should update a transaction status`() {
        // Given
        Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultPartnerExternalId)).thenReturn(defaultTransaction)
        Mockito.`when`(transactionGatewayMock.updateTransactionStatus(defaultTransaction, defaultStatus))
            .thenReturn(defaultUpdatedTransaction)
        doNothing().`when`(eventProviderMock).publish(
            defaultEventProjectId,
            defaultEventTopicId,
            defaultTransactionFinalizedEvent,
            defaultTransactionFinalizedEventAttributes
        )

        // When
        updateTransactionStatusUseCase.execute(defaultPartnerExternalId, defaultPartnerStatus)

        // Then
        Mockito.verify(transactionGatewayMock, times(1))
            .getTransactionByPartnerExternalId(defaultPartnerExternalId)
        Mockito.verify(transactionGatewayMock, times(1))
            .updateTransactionStatus(defaultTransaction, defaultStatus)
        Mockito.verify(eventProviderMock, times(0)).publish(
            defaultEventProjectId,
            defaultEventTopicId,
            defaultTransactionFinalizedEvent,
            defaultTransactionFinalizedEventAttributes
        )
    }

    @Test
    fun `Should emit transaction finalized event when feature flag is ON`() {
        // Given
        val partnerStatus = "success"
        val status = TransactionStatusEnum.SUCCESS
        val updatedTransaction = defaultTransaction.copy(status = status)
        val transactionFinalizedEvent = TransactionFinalizedEvent(updatedTransaction)
        val transactionFinalizedEventAttributes = hashMapOf(
            "status" to status.toString(),
            "sourceProductReferenceName" to updatedTransaction.origin.sourceProductReferenceName
        )
        Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultPartnerExternalId)).thenReturn(defaultTransaction)
        Mockito.`when`(transactionGatewayMock.updateTransactionStatus(defaultTransaction, status))
            .thenReturn(updatedTransaction)
        doNothing().`when`(eventProviderMock).publish(
            defaultEventProjectId,
            defaultEventTopicId,
            transactionFinalizedEvent,
            transactionFinalizedEventAttributes
        )

        // When
        updateTransactionStatusUseCase.execute(defaultPartnerExternalId, partnerStatus)

        // Then
        Mockito.verify(transactionGatewayMock, times(1))
            .getTransactionByPartnerExternalId(defaultPartnerExternalId)
        Mockito.verify(transactionGatewayMock, times(1))
            .updateTransactionStatus(defaultTransaction, status)
        Mockito.verify(eventProviderMock, times(1)).publish(
            defaultEventProjectId,
            defaultEventTopicId,
            transactionFinalizedEvent,
            transactionFinalizedEventAttributes
        )
    }

    @Test
    fun `Should not emit transaction finalized event when feature flag is OFF`() {
        // Given
        val partnerStatus = "success"
        val status = TransactionStatusEnum.SUCCESS
        val updatedTransaction = defaultTransaction.copy(status = status)
        val transactionFinalizedEvent = TransactionFinalizedEvent(updatedTransaction)
        val transactionFinalizedEventAttributes = hashMapOf(
            "status" to status.toString(),
            "sourceProductReferenceName" to updatedTransaction.origin.sourceProductReferenceName
        )
        Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultPartnerExternalId)).thenReturn(defaultTransaction)
        Mockito.`when`(transactionGatewayMock.updateTransactionStatus(defaultTransaction, status))
            .thenReturn(updatedTransaction)
        Mockito.`when`(featureFlagsConfigurationsMock.emitTransactionFinalizedEvent).thenReturn(false)
        doNothing().`when`(eventProviderMock).publish(
            defaultEventProjectId,
            defaultEventTopicId,
            transactionFinalizedEvent,
            transactionFinalizedEventAttributes
        )

        // When
        updateTransactionStatusUseCase.execute(defaultPartnerExternalId, partnerStatus)

        // Then
        Mockito.verify(transactionGatewayMock, times(1))
            .getTransactionByPartnerExternalId(defaultPartnerExternalId)
        Mockito.verify(transactionGatewayMock, times(1))
            .updateTransactionStatus(defaultTransaction, status)
        Mockito.verify(eventProviderMock, times(0)).publish(
            defaultEventProjectId,
            defaultEventTopicId,
            transactionFinalizedEvent,
            transactionFinalizedEventAttributes
        )
    }

    @Test
    fun `Should throw if transaction finalized event fails`() {
        // Given
        val partnerStatus = "success"
        val status = TransactionStatusEnum.SUCCESS
        val updatedTransaction = defaultTransaction.copy(status = status)
        val transactionFinalizedEvent = TransactionFinalizedEvent(updatedTransaction)
        val transactionFinalizedEventAttributes = hashMapOf(
            "status" to status.toString(),
            "sourceProductReferenceName" to updatedTransaction.origin.sourceProductReferenceName
        )
        Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultPartnerExternalId)).thenReturn(defaultTransaction)
        Mockito.`when`(transactionGatewayMock.updateTransactionStatus(defaultTransaction, status))
            .thenReturn(updatedTransaction)
        Mockito.`when`(eventProviderMock.publish(
            defaultEventProjectId,
            defaultEventTopicId,
            transactionFinalizedEvent,
            transactionFinalizedEventAttributes
        )).thenAnswer { throw Exception() }

        // When
        assertThrows<Exception> {
            updateTransactionStatusUseCase.execute(defaultPartnerExternalId, partnerStatus)
        }

        // Then
        Mockito.verify(transactionGatewayMock, times(1))
            .getTransactionByPartnerExternalId(defaultPartnerExternalId)
        Mockito.verify(transactionGatewayMock, times(1))
            .updateTransactionStatus(defaultTransaction, status)
        Mockito.verify(eventProviderMock, times(1)).publish(
            defaultEventProjectId,
            defaultEventTopicId,
            transactionFinalizedEvent,
            transactionFinalizedEventAttributes
        )
    }

    @Test
    fun `Should throw if transaction is not found`() {
        // Given
        Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultPartnerExternalId)).thenReturn(null)

        // When
        val exception = assertThrows<InternalErrorException> {
            updateTransactionStatusUseCase.execute(defaultPartnerExternalId, defaultPartnerStatus)
        }

        // Then
        val expectedErrorMessage = UpdateTransactionStatusErrorEnum.TRANSACTION_NOT_FOUND.toString()
        assertEquals(expectedErrorMessage, exception.message)
        Mockito.verify(transactionGatewayMock, times(1))
            .getTransactionByPartnerExternalId(defaultPartnerExternalId)
        Mockito.verify(transactionGatewayMock, times(0))
            .updateTransactionStatus(defaultTransaction, defaultStatus)
        Mockito.verify(eventProviderMock, times(0)).publish(
            defaultEventProjectId,
            defaultEventTopicId,
            defaultTransactionFinalizedEvent,
            defaultTransactionFinalizedEventAttributes
        )
    }

    @Test
    fun `Should throw if get transaction fails`() {
        // Given
        Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultPartnerExternalId))
            .thenAnswer { throw Exception() }


        // When
        assertThrows<Exception> {
            updateTransactionStatusUseCase.execute(defaultPartnerExternalId, defaultPartnerStatus)
        }

        // Then
        Mockito.verify(transactionGatewayMock, times(1))
            .getTransactionByPartnerExternalId(defaultPartnerExternalId)
        Mockito.verify(transactionGatewayMock, times(0))
            .updateTransactionStatus(defaultTransaction, defaultStatus)
        Mockito.verify(eventProviderMock, times(0)).publish(
            defaultEventProjectId,
            defaultEventTopicId,
            defaultTransactionFinalizedEvent,
            defaultTransactionFinalizedEventAttributes
        )
    }

    @Test
    fun `Should do nothing if current transaction status is not updatable`() {
        // Given
        val status = TransactionStatusEnum.SUCCESS
        val transaction = TransactionBuilder.build().copy(status = status)
        val transactionFinalizedEvent = TransactionFinalizedEvent(transaction)
        val transactionFinalizedEventAttributes = hashMapOf(
            "status" to status.toString(),
            "sourceProductReferenceName" to transaction.origin.sourceProductReferenceName
        )
        Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultPartnerExternalId)).thenReturn(transaction)

        // When
        updateTransactionStatusUseCase.execute(defaultPartnerExternalId, defaultPartnerStatus)

        // Then
        Mockito.verify(transactionGatewayMock, times(1))
            .getTransactionByPartnerExternalId(defaultPartnerExternalId)
        Mockito.verify(transactionGatewayMock, times(0))
            .updateTransactionStatus(defaultTransaction, TransactionStatusEnum.CREATED)
        Mockito.verify(eventProviderMock, times(0)).publish(
            defaultEventProjectId,
            defaultEventTopicId,
            transactionFinalizedEvent,
            transactionFinalizedEventAttributes
        )
    }

    @Test
    fun `Should throw if transaction status is not updated`() {
        // Given
        Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultPartnerExternalId)).thenReturn(defaultTransaction)
        Mockito.`when`(transactionGatewayMock.updateTransactionStatus(defaultTransaction, defaultStatus)).thenReturn(null)

        // When
        assertThrows<InternalErrorException> {
            updateTransactionStatusUseCase.execute(defaultPartnerExternalId, defaultPartnerStatus)
        }

        // Then
        Mockito.verify(transactionGatewayMock, times(1))
            .getTransactionByPartnerExternalId(defaultPartnerExternalId)
        Mockito.verify(transactionGatewayMock, times(1))
            .updateTransactionStatus(defaultTransaction, TransactionStatusEnum.CREATED)
        Mockito.verify(eventProviderMock, times(0)).publish(
            defaultEventProjectId,
            defaultEventTopicId,
            defaultTransactionFinalizedEvent,
            defaultTransactionFinalizedEventAttributes
        )
    }

    @Test
    fun `Should throw if transaction status update fails`() {
        // Given
        Mockito.`when`(transactionGatewayMock.getTransactionByPartnerExternalId(defaultPartnerExternalId)).thenReturn(defaultTransaction)
        Mockito.`when`(transactionGatewayMock.updateTransactionStatus(defaultTransaction, defaultStatus)).thenAnswer { throw Exception() }

        // When
        assertThrows<Exception> {
            updateTransactionStatusUseCase.execute(defaultPartnerExternalId, defaultPartnerStatus)
        }

        // Then
        Mockito.verify(transactionGatewayMock, times(1))
            .getTransactionByPartnerExternalId(defaultPartnerExternalId)
        Mockito.verify(transactionGatewayMock, times(1))
            .updateTransactionStatus(defaultTransaction, TransactionStatusEnum.CREATED)
        Mockito.verify(eventProviderMock, times(0)).publish(
            defaultEventProjectId,
            defaultEventTopicId,
            defaultTransactionFinalizedEvent,
            defaultTransactionFinalizedEventAttributes
        )
    }

    @Test
    fun `Should return if transaction status is unmapped`() {
        // Given
        val status = "invalid_status"

        // When
        updateTransactionStatusUseCase.execute(defaultPartnerExternalId, status)

        // Then
        Mockito.verify(transactionGatewayMock, never())
            .getTransactionByPartnerExternalId(defaultPartnerExternalId)
        Mockito.verify(transactionGatewayMock, never())
            .updateTransactionStatus(defaultTransaction, TransactionStatusEnum.CREATED)
        Mockito.verify(eventProviderMock, never()).publish(
            eq(defaultEventProjectId),
            eq(defaultEventTopicId),
            any(),
            eq(defaultTransactionFinalizedEventAttributes)
        )
    }
}