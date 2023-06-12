package br.com.ume.application.shared.events.dtos

data class Event(
    val attributes: Map<String, String>?,
    val data: Any,
)

data class EventDestination(
    val topic: String,
    val projectId: String,
)