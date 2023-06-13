package br.com.ume.api.configs

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Requires

@ConfigurationProperties(PixApiConfigurations.CONFIG_PREFIX)
@Requires(property = PixApiConfigurations.CONFIG_PREFIX)
class PixApiConfigurations {
    var apiKey: String? = null
    var apiUrl: String? = null

    companion object {
        const val CONFIG_PREFIX = "config.apis.pixApi"
    }
}