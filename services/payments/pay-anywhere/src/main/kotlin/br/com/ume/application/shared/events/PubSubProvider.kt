package br.com.ume.application.shared.events

import br.com.ume.application.shared.events.dtos.Event
import br.com.ume.application.shared.events.dtos.EventDestination
import br.com.ume.application.shared.utils.CustomSerializer
import br.com.ume.application.shared.utils.retryAndLog
import com.google.cloud.pubsub.v1.Publisher
import com.google.protobuf.ByteString
import com.google.pubsub.v1.PubsubMessage
import com.google.pubsub.v1.TopicName
import io.micronaut.runtime.http.scope.RequestScope
import java.util.concurrent.TimeUnit

@RequestScope
class PubSubProvider (
    private val publisherBuilder: PublisherBuilder,
): EventProvider {
    companion object {
        private const val maxNumberOfAttempts: Int = 3
    }

    override fun publish(projectId: String, topicId: String, data: Any, attributes: Map<String, String>?) {
        var publisher: Publisher? = null
        try {
            val topicName = TopicName.of(projectId, topicId)

            val destination = EventDestination(topicId, projectId)
            val event = Event(attributes, data)

            publisher = retryAndLog("publishing event message", maxNumberOfAttempts,
                mapOf("destination" to destination, "event" to event)) {
                buildMessageAndPublish(data, attributes, topicName)
            }
        } finally {
            publisher?.let {
                it.shutdown()
                it.awaitTermination(1, TimeUnit.MINUTES)
            }
        }
    }

    private fun buildMessageAndPublish(
        data: Any,
        attributes: Map<String, String>?,
        topicName: TopicName
    ): Publisher {
        val publisher = publisherBuilder.build(topicName)

        var pubsubMessageBuilder = PubsubMessage.newBuilder()
            .setData(ByteString.copyFromUtf8(CustomSerializer.serialize(data)))
        attributes?.let { pubsubMessageBuilder = pubsubMessageBuilder.putAllAttributes(attributes) }

        publisher.publish(pubsubMessageBuilder.build()).get()
        return publisher
    }
}