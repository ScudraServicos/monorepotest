package br.com.ume.api.eventStream.transport

import br.com.ume.api.eventStream.deserializers.EventStreamDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.util.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Null
import kotlin.collections.HashMap

data class EventMessage<T>(
    @field: Null
    val attributes: HashMap<String, String>?,

    @field: NotNull @JsonDeserialize(using = EventStreamDeserializer::class)
    val data: T,

    @field: NotNull
    val messageId: String,

    @field: NotNull
    val publishTime: Date
)
