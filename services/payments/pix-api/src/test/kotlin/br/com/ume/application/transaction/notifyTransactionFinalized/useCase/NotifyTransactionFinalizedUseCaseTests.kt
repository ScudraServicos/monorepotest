package br.com.ume.application.transaction.notifyTransactionFinalized.useCase

import br.com.ume.api.configs.NotificationsConfigurations
import br.com.ume.application.features.transaction.notifyTransactionFinalized.dtos.TransactionFinalizedNotificationParameters
import br.com.ume.application.features.transaction.notifyTransactionFinalized.dtos.TransactionFinalizedNotification
import br.com.ume.application.features.transaction.notifyTransactionFinalized.useCase.NotifyTransactionFinalizedUseCase
import br.com.ume.application.features.transaction.notifyTransactionFinalized.useCase.NotifyTransactionFinalizedUseCaseImpl
import br.com.ume.application.shared.events.EventProvider
import br.com.ume.application.shared.transaction.testBuilders.TransactionBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.times

class NotifyTransactionFinalizedUseCaseTests {
    private lateinit var notificationsConfigurationsMock: NotificationsConfigurations
    private lateinit var eventProviderMock: EventProvider
    private lateinit var notifyTransactionFinalizedUseCase: NotifyTransactionFinalizedUseCase

    @BeforeEach
    fun setUp() {
        notificationsConfigurationsMock = Mockito.mock(NotificationsConfigurations::class.java)
        eventProviderMock = Mockito.mock(EventProvider::class.java)
        notifyTransactionFinalizedUseCase = NotifyTransactionFinalizedUseCaseImpl(eventProviderMock, notificationsConfigurationsMock)
    }

    @Test
    fun `Should not notify in case the transaction don't have a borrowerId`() {
        // Given
        val transaction =  TransactionBuilder.build("1", null)

        // When
        notifyTransactionFinalizedUseCase.execute(transaction)

        // Then
        Mockito.verify(eventProviderMock, times(0))
            .publish(any(), any(), any(), any())
    }

    @Test
    fun `Should notify transaction finalization`() {
        // Given
        val transaction =  TransactionBuilder.build()
        val topicId = "topicId"
        val projectId = "projectId"
        val event = "event"
        val eventData = TransactionFinalizedNotification(
            transaction.origin.userId!!,
            event,
            TransactionFinalizedNotificationParameters(
                "20,00",
                transaction.status.toString()
            )
        )

        Mockito.`when`(notificationsConfigurationsMock.topicId).thenReturn(topicId)
        Mockito.`when`(notificationsConfigurationsMock.projectId).thenReturn(projectId)
        Mockito.`when`(notificationsConfigurationsMock.transactionFinalizedEventName).thenReturn(event)

        // When
        notifyTransactionFinalizedUseCase.execute(transaction)

        // Then
        Mockito.verify(eventProviderMock, times(1))
            .publish(projectId, topicId, eventData, hashMapOf("status" to transaction.status.toString()))
    }

    @Test
    fun `Should fail if event publishing throws exception`() {
        // Given
        val transaction =  TransactionBuilder.build()
        val topicId = "topicId"
        val projectId = "projectId"
        val event = "event"
        val eventData = TransactionFinalizedNotification(
            transaction.origin.userId!!,
            event,
            TransactionFinalizedNotificationParameters(
                "20,00",
                transaction.status.toString()
            )
        )
        val arguments = hashMapOf("status" to transaction.status.toString())

        Mockito.`when`(notificationsConfigurationsMock.topicId).thenReturn(topicId)
        Mockito.`when`(notificationsConfigurationsMock.projectId).thenReturn(projectId)
        Mockito.`when`(notificationsConfigurationsMock.transactionFinalizedEventName).thenReturn(event)
        Mockito.`when`(eventProviderMock.publish(projectId, topicId, eventData, arguments))
            .thenAnswer { throw Exception() }

        // When
        assertThrows<Exception> {
            notifyTransactionFinalizedUseCase.execute(transaction)
        }

        // Then
        Mockito.verify(eventProviderMock, times(1))
            .publish(projectId, topicId, eventData, arguments)
    }
}