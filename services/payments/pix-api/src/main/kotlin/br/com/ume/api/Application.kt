package br.com.ume.api

import io.micronaut.context.ApplicationContextBuilder
import io.micronaut.context.ApplicationContextConfigurer
import io.micronaut.context.annotation.ContextConfigurer
import io.micronaut.core.annotation.NonNull
import io.micronaut.runtime.Micronaut.run
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info


@ContextConfigurer
class DefaultEnvironmentConfigurer : ApplicationContextConfigurer {
    override fun configure(@NonNull builder: ApplicationContextBuilder) {
        builder
            .deduceEnvironment(false)
            .defaultEnvironments("local")
    }
}

@OpenAPIDefinition(
    info = Info(
        title = "PIX - API",
        description = "API responsible for PIX Transactions"
    )
)
object Api {
}
fun main(args: Array<String>) {
    run(*args)
}