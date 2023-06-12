package br.com.ume.api.configs

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Requires

@ConfigurationProperties(AuthenticationConfiguration.CONFIG_PREFIX)
@Requires(property = AuthenticationConfiguration.CONFIG_PREFIX)
class AuthenticationConfiguration {
    var apiKey: String? = null

    companion object {
        const val CONFIG_PREFIX = "config.authentication"
    }
}