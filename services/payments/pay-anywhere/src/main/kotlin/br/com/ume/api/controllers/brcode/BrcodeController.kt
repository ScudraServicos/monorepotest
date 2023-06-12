package br.com.ume.api.controllers.brcode

import br.com.ume.api.controllers.brcode.dtos.InspectBrCodeRequest
import br.com.ume.application.features.brcode.inspectBrcode.useCase.InspectBrcodeUseCase
import br.com.ume.application.features.brcode.inspectBrcode.useCase.InspectBrcodeUseCaseImpl
import br.com.ume.application.features.brcode.inspectBrcode.useCase.dtos.InspectedBrcodeDto
import br.com.ume.libs.logging.gcp.LoggerFactory
import io.micronaut.core.version.annotation.Version
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.RequestBean
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
    private val inspectBrcodeUseCase: InspectBrcodeUseCase
) {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(BrcodeController::class.java.name)
    }

    @Get("inspect")
    @Operation(summary = "Get inspected brcode", description = "Get BRCode payment details")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Brcode details"),
        ApiResponse(responseCode = "400", description = "Brcode is invalid for Pay Anywhere"),
        ApiResponse(responseCode = "500", description = "Error")
    )
    fun inspectBrcode(@RequestBean request: InspectBrCodeRequest): InspectedBrcodeDto {
        MDC.put("useCase", InspectBrcodeUseCaseImpl::class.java.simpleName)
        MDC.put("brcode", request.brcode)
        MDC.put("userId", request.userId)
        log.info("Starting UseCase")

        return inspectBrcodeUseCase.execute(request.brcode, request.userId)
    }
}