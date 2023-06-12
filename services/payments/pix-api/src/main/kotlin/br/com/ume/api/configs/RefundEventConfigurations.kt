package br.com.ume.api.configs

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Requires

@ConfigurationProperties(RefundEventConfigurations.CONFIG_PREFIX)
@Requires(property = RefundEventConfigurations.CONFIG_PREFIX)
class RefundEventConfigurations {
    var projectId: String? = null
    var topicId: String? = null

    companion object {
        const val CONFIG_PREFIX = "config.events.refund"
    }
}