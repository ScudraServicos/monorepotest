package br.com.ume.application.shared.accessControl.repository

import br.com.ume.application.shared.accessControl.domain.AccessControl
import br.com.ume.libs.logging.gcp.JsonLogBuilder
import br.com.ume.libs.logging.gcp.LoggerFactory
import io.micronaut.runtime.http.scope.RequestScope
import java.lang.Exception
import java.util.logging.Logger

@RequestScope
class AccessControlRepositoryImpl(
    private val accessControlRepository: AccessControlJpaRepository
) : AccessControlRepository {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(AccessControlRepositoryImpl::class.java.name)
    }

    override fun getAccessControl(userId: String): AccessControl? {
        try {
            val accessControl = accessControlRepository.findById(userId)
            if (accessControl.isEmpty)
                return AccessControl(userId = userId, emptySet(), allowed = false)
            return AccessControl(accessControl.get())
        } catch (exception: Exception) {
            log.severe(JsonLogBuilder.build(object {
                val message = "Unexpected error while trying to get access control"
                val userId = userId
                val exception = exception
            }))
            return null
        }
    }
}