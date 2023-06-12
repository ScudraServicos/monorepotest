package br.com.ume.api.configs

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Requires

@ConfigurationProperties(BrcodeValidationConfiguration.CONFIG_PREFIX)
@Requires(property = BrcodeValidationConfiguration.CONFIG_PREFIX)
class BrcodeValidationConfiguration {
    var legalPersonBlockList: HashSet<String>? = null
    var minimumValue: Double? = null

    companion object {
        const val CONFIG_PREFIX = "config.brcodeValidation"
    }
}