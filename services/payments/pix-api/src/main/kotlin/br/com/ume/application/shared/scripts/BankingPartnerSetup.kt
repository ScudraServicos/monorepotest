package br.com.ume.application.shared.scripts

import br.com.ume.api.configs.BankingPartnerConfigurations
import br.com.ume.application.shared.logging.gcp.LoggerFactory
import com.starkbank.Project
import com.starkbank.Settings
import io.micronaut.context.annotation.Requires
import io.micronaut.context.env.Environment
import io.micronaut.runtime.event.annotation.EventListener
import jakarta.inject.Singleton
import io.micronaut.discovery.event.ServiceReadyEvent
import java.nio.file.Files
import java.nio.file.Paths
import java.util.logging.Logger

@Singleton
@Requires(notEnv = [Environment.TEST])
class BankingPartnerSetup(private val config: BankingPartnerConfigurations) {
    private val log: Logger = LoggerFactory.buildStructuredLogger(BankingPartnerSetup::class.java.name)

    @EventListener
    fun setupBankingPartner(event: ServiceReadyEvent) {
        log.info("Setting banking partner credentials")

        val project = Project(
            config.environment,
            config.projectId,
            getPrivateKey()
        )

        Settings.user = project
        Settings.language = "pt-BR";

        log.info("[DONE] Set banking partner credentials for ${config.environment}")
    }

    private fun getPrivateKey(): String {
        return String(Files.readAllBytes(Paths.get(config.privateKeyPath)))
    }
}