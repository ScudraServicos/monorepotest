package br.com.ume.application.shared.events

interface EventProvider {
    fun publish(projectId: String, topicId: String, data: Any, attributes: Map<String, String>?)
}