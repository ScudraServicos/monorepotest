package br.com.ume.api.eventStream.deserializers

import br.com.ume.application.shared.logging.gcp.JsonLogBuilder
import br.com.ume.application.shared.logging.gcp.LoggerFactory
import br.com.ume.application.utils.CustomDeserializer
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import io.micronaut.runtime.http.scope.RequestScope
import java.util.Base64
import java.util.logging.Logger

@RequestScope
class EventStreamDeserializer : JsonDeserializer<Any?>(), ContextualDeserializer {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(EventStreamDeserializer::class.java.name)
    }
    private var targetClass: Class<*>? = null

    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext?): Any {
        try {
            val nodeContent: String = parser.codec.readTree<JsonNode?>(parser).textValue()
            val rawObject = String(Base64.getDecoder().decode(nodeContent))
            return CustomDeserializer.deserialize(rawObject, targetClass!!)
        } catch (ex: Exception) {
            log.severe(JsonLogBuilder.build(object {
                val message = "Unexpected error while trying to deserialize field: ${parser.currentName}"
                val exception = ex
            }))
            throw ex
        }
    }

    override fun createContextual(ctxt: DeserializationContext, property: BeanProperty?): JsonDeserializer<*> {
        targetClass = ctxt.contextualType.rawClass
        return this
    }
}