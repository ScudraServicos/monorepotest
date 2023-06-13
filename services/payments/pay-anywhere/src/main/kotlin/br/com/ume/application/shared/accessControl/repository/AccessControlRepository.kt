package br.com.ume.application.shared.accessControl.repository

import br.com.ume.application.shared.accessControl.domain.AccessControl

interface AccessControlRepository {
    fun getAccessControl(userId: String): AccessControl?
}