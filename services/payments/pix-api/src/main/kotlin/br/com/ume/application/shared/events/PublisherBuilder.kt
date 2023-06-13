package br.com.ume.application.shared.events

import com.google.cloud.pubsub.v1.Publisher
import com.google.pubsub.v1.TopicName
import io.micronaut.runtime.http.scope.RequestScope

@RequestScope
class PublisherBuilder {
    fun build(topicName: TopicName): Publisher {
        return Publisher.newBuilder(topicName).build()
    }
}