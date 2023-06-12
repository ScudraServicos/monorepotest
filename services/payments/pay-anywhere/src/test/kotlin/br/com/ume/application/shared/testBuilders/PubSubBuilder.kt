package br.com.ume.application.shared.testBuilders

import br.com.ume.application.shared.utils.CustomSerializer
import com.google.protobuf.ByteString
import com.google.pubsub.v1.PubsubMessage

class PubSubBuilder {
    companion object {
        fun buildPubSubMessage(data: Any, attributes: Map<String, String>?) : PubsubMessage {
            var pubsubMessageBuilder = PubsubMessage.newBuilder()
                .setData(ByteString.copyFromUtf8(CustomSerializer.serialize(data)))
            attributes?.let { pubsubMessageBuilder = pubsubMessageBuilder.putAllAttributes(attributes) }
            return pubsubMessageBuilder.build()
        }
    }
}