package br.com.ume.application.shared.configs

import br.com.ume.application.shared.accessControl.domain.AccessControlUserGroup
import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("config")
class AccessControlUserGroupConfiguration {
    var accessControlUserGroups: List<AccessControlUserGroup> = emptyList()
}