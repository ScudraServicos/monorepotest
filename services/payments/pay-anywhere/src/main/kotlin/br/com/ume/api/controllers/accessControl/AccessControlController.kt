package br.com.ume.api.controllers.accessControl

import br.com.ume.application.features.accessControl.getAccessControl.useCase.GetAccessControlUseCaseImpl
import br.com.ume.application.features.accessControl.getAccessControl.useCase.GetAccessControlUseCase
import br.com.ume.libs.logging.gcp.LoggerFactory
import io.micronaut.core.version.annotation.Version
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.validation.Validated
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.MDC
import java.util.logging.Logger

@Controller("/api/access-control")
@Validated
@Version("1")
@Tag(name = "Access Control")
class AccessControlController(
    private val accessControlUseCase: GetAccessControlUseCase
) {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(AccessControlController::class.java.name)
    }

    @Get("{userId}")
    @Operation(summary = "Get access control for user", description = "Returns if user is allowed to access the product")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "User allowed"),
        ApiResponse(responseCode = "403", description = "User denied")
    )
    fun getAccessControl(userId: String) {
        MDC.put("useCase", GetAccessControlUseCaseImpl::class.java.simpleName)
        MDC.put("userId", userId)
        log.info("Starting UseCase")

        return accessControlUseCase.execute(userId)
    }
}