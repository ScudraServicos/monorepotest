package br.com.ume.api.configs

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Requires

@ConfigurationProperties(RefundConfigurations.CONFIG_PREFIX)
@Requires(property = RefundConfigurations.CONFIG_PREFIX)
class RefundConfigurations {
    var paymentLeftoverCutoffValue: Double? = null

    companion object {
        const val CONFIG_PREFIX = "config.refund"
    }
}