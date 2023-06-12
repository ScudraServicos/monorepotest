package br.com.ume.application.shared.accessControl.utils

import br.com.ume.application.shared.accessControl.domain.AccessControlUserGroup
import br.com.ume.application.shared.configs.AccessControlUserGroupConfiguration
import jakarta.inject.Singleton

// TODO(etevaldo.melo): make this class static when using mockk.
@Singleton
class AccessControlUserGroupRetriever(private val config: AccessControlUserGroupConfiguration) {

    private val groupsMap: Map<String, AccessControlUserGroup> = buildGroupsMap()

    private fun buildGroupsMap(): Map<String, AccessControlUserGroup> =
        config.accessControlUserGroups.associateBy { it.name }

    fun get(group: String): AccessControlUserGroup? {
        return groupsMap[group]
    }
}