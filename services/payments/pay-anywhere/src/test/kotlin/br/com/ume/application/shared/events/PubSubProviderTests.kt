package br.com.ume.application.shared.events

import br.com.ume.application.shared.testBuilders.PubSubBuilder
import com.google.api.core.ApiFutures
import com.google.cloud.pubsub.v1.Publisher
import com.google.pubsub.v1.TopicName
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.concurrent.TimeUnit

class PubSubProviderTests {
    private lateinit var publisherBuilderMock: PublisherBuilder
    private lateinit var publisherMock: Publisher
    private lateinit var pubSubProvider: EventProvider

    companion object {
        private const val defaultProjectId: String = "projectId"
        private const val defaultTopicId: String = "topicId"
        private val defaultTopicName = TopicName.of(defaultProjectId, defaultTopicId)
        private const val eventData: String = "projectId"
        private val defaultEventAttributes = emptyMap<String, String>()
    }

    @BeforeEach
    fun setUp() {
        publisherBuilderMock = Mockito.mock(PublisherBuilder::class.java)
        publisherMock = Mockito.mock(Publisher::class.java)
        pubSubProvider = PubSubProvider(publisherBuilderMock)
    }

    @Nested
    @DisplayName("publish()")
    inner class Publish {
        @Test
        fun `Should publish an event successfully in first try`() {
            val eventMessage = PubSubBuilder.buildPubSubMessage(eventData, null)
            Mockito.`when`(publisherMock.publish(eventMessage)).thenReturn(ApiFutures.immediateFuture("123"))
            Mockito.doNothing().`when`(publisherMock).shutdown()
            Mockito.`when`(publisherMock.awaitTermination(1, TimeUnit.MINUTES)).thenReturn(true)
            Mockito.`when`(publisherBuilderMock.build(defaultTopicName)).thenReturn(publisherMock)

            pubSubProvider.publish(defaultProjectId, defaultTopicId, eventData, defaultEventAttributes)

            Mockito.verify(publisherMock, Mockito.times(1)).publish(eventMessage)
            Mockito.verify(publisherMock, Mockito.times(1)).shutdown()
            Mockito.verify(publisherMock, Mockito.times(1)).awaitTermination(1, TimeUnit.MINUTES)
            Mockito.verify(publisherBuilderMock, Mockito.times(1)).build(defaultTopicName)
        }

        @Test
        fun `Should publish an event successfully in N-th try`() {
            val eventMessage = PubSubBuilder.buildPubSubMessage(eventData, null)
            Mockito.`when`(publisherMock.publish(eventMessage))
                .thenThrow(RuntimeException("ERROR"))
                .thenReturn(ApiFutures.immediateFuture("123"))
            Mockito.doNothing().`when`(publisherMock).shutdown()
            Mockito.`when`(publisherMock.awaitTermination(1, TimeUnit.MINUTES)).thenReturn(true)
            Mockito.`when`(publisherBuilderMock.build(defaultTopicName)).thenReturn(publisherMock)

            pubSubProvider.publish(defaultProjectId, defaultTopicId, eventData, defaultEventAttributes)

            Mockito.verify(publisherMock, Mockito.times(2)).publish(eventMessage)
            Mockito.verify(publisherMock, Mockito.times(1)).shutdown()
            Mockito.verify(publisherMock, Mockito.times(1)).awaitTermination(1, TimeUnit.MINUTES)
            Mockito.verify(publisherBuilderMock, Mockito.times(2)).build(defaultTopicName)
        }

        @Test
        fun `Should log event parameters when publish fail in all tries`() {
            val eventMessage = PubSubBuilder.buildPubSubMessage(eventData, null)
            Mockito.`when`(publisherMock.publish(eventMessage)).thenThrow(RuntimeException("ERROR"))
            Mockito.`when`(publisherBuilderMock.build(defaultTopicName)).thenReturn(publisherMock)

            pubSubProvider.publish(defaultProjectId, defaultTopicId, eventData, defaultEventAttributes)

            Mockito.verify(publisherMock, Mockito.times(0)).shutdown()
            Mockito.verify(publisherMock, Mockito.times(0)).awaitTermination(1, TimeUnit.MINUTES)
            Mockito.verify(publisherBuilderMock, Mockito.times(3)).build(defaultTopicName)
        }
    }
}