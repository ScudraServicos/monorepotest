package br.com.ume.api.configs

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Requires

@ConfigurationProperties(TransactionFinalizedTopicConfigurations.CONFIG_PREFIX)
@Requires(property = TransactionFinalizedTopicConfigurations.CONFIG_PREFIX)
class TransactionFinalizedTopicConfigurations {
    var projectId: String? = null
    var topicId: String? = null

    companion object {
        const val CONFIG_PREFIX = "config.events.transactionFinalizedTopic"
    }
}