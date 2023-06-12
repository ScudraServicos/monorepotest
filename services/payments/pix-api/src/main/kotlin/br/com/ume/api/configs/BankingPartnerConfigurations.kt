package br.com.ume.api.configs

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Requires
import io.micronaut.context.env.Environment

@ConfigurationProperties(BankingPartnerConfigurations.CONFIG_PREFIX)
@Requires(property = BankingPartnerConfigurations.CONFIG_PREFIX, notEnv = [Environment.TEST])
class BankingPartnerConfigurations {
    var environment: String? = null
    var projectId: String? = null
    var privateKeyPath: String? = null

    companion object {
        const val CONFIG_PREFIX = "config.bankingPartner"
    }
}