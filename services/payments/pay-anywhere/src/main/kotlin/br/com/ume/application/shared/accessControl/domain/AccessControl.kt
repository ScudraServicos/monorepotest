package br.com.ume.application.shared.accessControl.domain

import br.com.ume.application.shared.accessControl.repository.dtos.AccessControlDto
import br.com.ume.libs.logging.gcp.JsonLogBuilder
import br.com.ume.libs.logging.gcp.LoggerFactory
import java.util.logging.Logger

class AccessControl(
    val userId: String,
    val groups: Set<String>,
    private val allowed: Boolean,
) {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(AccessControl::class.java.name)

        private fun mapGroups(userId: String, groups: String): Set<String> {
            return try {
                groups.split(',').toSet()
            } catch (exception: Exception) {
                log.severe(JsonLogBuilder.build(object {
                    val message = "Error mapping access control groups"
                    val userId = userId
                }))
                emptySet()
            }
        }
    }

    constructor(accessControlDto: AccessControlDto) : this(
        userId = accessControlDto.userId,
        allowed = accessControlDto.allowed,
        groups = mapGroups(accessControlDto.userId, accessControlDto.groups)
    )

    fun notAllowed(): Boolean {
        return !this.allowed
    }
}
