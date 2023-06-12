package br.com.ume.application.shared.accessControl.domain

data class AccessControlUserGroup(
    val name: String,
    val dependsOnStore: Boolean,
    val storeDocumentsSet: Set<String>,
)