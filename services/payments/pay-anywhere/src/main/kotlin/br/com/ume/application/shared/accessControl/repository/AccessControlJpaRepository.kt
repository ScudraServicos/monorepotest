package br.com.ume.application.shared.accessControl.repository

import br.com.ume.application.shared.accessControl.repository.dtos.AccessControlDto
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface AccessControlJpaRepository: JpaRepository<AccessControlDto, String> {
}