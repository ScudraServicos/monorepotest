package br.com.ume.api.configs

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Requires

@ConfigurationProperties(FeatureFlagsConfigurations.CONFIG_PREFIX)
@Requires(property = FeatureFlagsConfigurations.CONFIG_PREFIX)
class FeatureFlagsConfigurations {
    var emitTransactionFinalizedEvent: Boolean? = null

    companion object {
        const val CONFIG_PREFIX = "config.featureFlags"
    }
}