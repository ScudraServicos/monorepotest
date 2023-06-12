package br.com.ume.api.configs

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Requires

@ConfigurationProperties(NotificationsConfigurations.CONFIG_PREFIX)
@Requires(property = NotificationsConfigurations.CONFIG_PREFIX)
class NotificationsConfigurations {
    var projectId: String? = null
    var topicId: String? = null
    var transactionFinalizedEventName: String? = null

    companion object {
        const val CONFIG_PREFIX = "config.events.notifications"
    }
}