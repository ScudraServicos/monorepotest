package br.com.ume.api.configs

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Requires

@ConfigurationProperties(CoordinatorConfigurations.CONFIG_PREFIX)
@Requires(property = CoordinatorConfigurations.CONFIG_PREFIX)
class CoordinatorConfigurations {
    var apiUrl: String? = null
    var apiKey: String? = null

    companion object {
        const val CONFIG_PREFIX = "config.apis.coordinatorApi"
    }
}