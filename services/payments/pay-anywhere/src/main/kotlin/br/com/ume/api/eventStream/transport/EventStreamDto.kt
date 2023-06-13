package br.com.ume.api.eventStream.transport

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
data class EventStreamDto<T>(
    @field: NotBlank @field: NotNull
    val subscription: String,

    @field: NotNull
    val message: EventMessage<T>
)
