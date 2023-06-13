package br.com.ume.application.shared.accessControl.repository.dtos

import io.micronaut.core.annotation.Introspected
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import java.util.Date
import javax.persistence.*
import kotlin.reflect.KClass

@Entity
@Table(name = "ACCESS_CONTROL")
@Introspected
data class AccessControlDto(
    @Id
    @Column(name = "USER_ID")
    val userId: String,

    @Column(name = "ALLOWED")
    val allowed: Boolean,

    @Column(name = "CREATION_DATE")
    val creationDate: Date,

    @Column(name = "ALTERATION_DATE")
    val alterationDate: Date,

    @Column(name = "GROUPS")
    val groups: String,
)