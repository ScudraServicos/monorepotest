package br.com.ume.application.features.accessControl.getAccessControl.useCase

import br.com.ume.api.exceptions.types.ForbiddenException
import br.com.ume.application.shared.accessControl.gateway.AccessControlGateway
import br.com.ume.libs.logging.gcp.LoggerFactory
import io.micronaut.runtime.http.scope.RequestScope
import java.util.logging.Logger

@RequestScope
class GetAccessControlUseCaseImpl(
    private val accessControlGateway: AccessControlGateway
) : GetAccessControlUseCase {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(GetAccessControlUseCaseImpl::class.java.name)
    }

    override fun execute(userId: String) {
        val accessControl = accessControlGateway.getAccessControl(userId)
        if (accessControl == null || accessControl.notAllowed()) {
            log.info("User not allowed")
            throw ForbiddenException()
        }

        log.info("User allowed")
    }
}