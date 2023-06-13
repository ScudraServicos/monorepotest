package br.com.ume.application.shared.configs

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Requires

@ConfigurationProperties(PaymentsTopicConfigurations.CONFIG_PREFIX)
@Requires(property = PaymentsTopicConfigurations.CONFIG_PREFIX)
class PaymentsTopicConfigurations {
    var projectId: String? = null
    var topicId: String? = null

    companion object {
        const val CONFIG_PREFIX = "config.events.paymentsTopic"
    }
}