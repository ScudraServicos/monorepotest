package br.com.ume.application.shared.accessControl.gateway

import br.com.ume.application.shared.accessControl.domain.AccessControl
import br.com.ume.application.shared.accessControl.repository.AccessControlRepository
import io.micronaut.runtime.http.scope.RequestScope

@RequestScope
class AccessControlGatewayImpl(
    private val accessControlRepository: AccessControlRepository
): AccessControlGateway {
    companion object {
        private const val cacheKeyPrefix = "PayAnywhere.AccessControl"
    }

    override fun getAccessControl(userId: String): AccessControl? {
        val accessControl = accessControlRepository.getAccessControl(userId)

        return accessControl
    }
}