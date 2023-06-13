package br.com.ume.application.features.accessControl.getAccessControl.useCase

interface GetAccessControlUseCase {
    fun execute(userId: String): Unit
}