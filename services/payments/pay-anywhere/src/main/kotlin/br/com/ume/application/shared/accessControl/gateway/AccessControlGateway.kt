package br.com.ume.application.shared.accessControl.gateway

import br.com.ume.application.shared.accessControl.domain.AccessControl

interface AccessControlGateway {
    fun getAccessControl(userId: String): AccessControl?
}