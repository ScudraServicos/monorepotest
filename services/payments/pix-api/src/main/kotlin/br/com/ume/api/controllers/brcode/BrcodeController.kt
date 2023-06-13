package br.com.ume.api.controllers.brcode

import br.com.ume.application.features.brcode.inspectBrcode.useCase.InspectBrcodeUseCase
import br.com.ume.application.features.brcode.shared.brcodeInspected.domain.BrcodeInspected
import br.com.ume.application.shared.logging.gcp.LoggerFactory
import io.micronaut.core.version.annotation.Version
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.validation.Validated
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.MDC
import java.util.logging.Logger

@Controller("/api/brcode")
@Validated
@Version("1")
@Tag(name = "Brcode")
class BrcodeController(
    private val inspectBrcodeUseCase: InspectBrcodeUseCase,
) {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(BrcodeController::class.java.name)
    }

    @Get("inspect")
    @Operation(summary = "Get inspected brcode", description = "Get BRCode payment details")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Inspected brcode"),
        ApiResponse(responseCode = "400", description = "Invalid brcode"),
        ApiResponse(responseCode = "500", description = "Payment error")
    )
    fun inspectBrcode(@QueryValue brcode: String): BrcodeInspected {
        MDC.put("useCase", InspectBrcodeUseCase::class.java.simpleName)
        MDC.put("brcode", brcode)
        log.info("Starting UseCase")

        return inspectBrcodeUseCase.execute(brcode)
    }
}