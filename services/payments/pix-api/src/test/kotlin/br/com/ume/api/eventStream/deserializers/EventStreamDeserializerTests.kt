package br.com.ume.api.eventStream.deserializers

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.*
import java.util.*

class EventStreamDeserializerTests {
    @Nested
    @DisplayName("deserialize()")
    inner class Deserialize {
        @Test
        fun `Should successfully deserialize the field`() {
            // Given
            val payload = PayloadExample("example")
            val jsonPayload = jacksonObjectMapper().writeValueAsString(payload)
            val base64JsonPayload = Base64.getEncoder().encodeToString(jsonPayload.toByteArray())
            val input = """{"data": "$base64JsonPayload"}"""

            // When
            val result = jacksonObjectMapper().readValue(input, object : TypeReference<MessageExample<PayloadExample>>() {})

            // Then
            val expectedMessage = MessageExample(payload)
            Assertions.assertEquals(expectedMessage, result)
        }

        @Test
        fun `Should throw exception in case of a wrong formatted base64 string`() {
            // Given
            val base64JsonPayload = "notbase64"
            val input = """{"data": "$base64JsonPayload"}"""

            // When / Then
            assertThrows<JsonMappingException> {
                jacksonObjectMapper().readValue(input, object : TypeReference<MessageExample<PayloadExample>>() {})
            }
        }

        @Test
        fun `Should throw exception in case the field it's blank`() {
            // Given
            val base64JsonPayload = ""
            val input = """{"data": "$base64JsonPayload"}"""

            // When / Then
            assertThrows<JsonMappingException> {
                jacksonObjectMapper().readValue(input, object : TypeReference<MessageExample<PayloadExample>>() {})
            }
        }

        @Test
        fun `Should throw exception in case the field it's not sent`() {
            // Given
            val input = "{}"

            // When / Then
            assertThrows<JsonMappingException> {
                jacksonObjectMapper().readValue(input, object : TypeReference<MessageExample<PayloadExample>>() {})
            }
        }
    }

    data class MessageExample<T>(
        @JsonDeserialize(using = EventStreamDeserializer::class)
        val data: T
    )

    data class PayloadExample(
       val payload: String
    )
}