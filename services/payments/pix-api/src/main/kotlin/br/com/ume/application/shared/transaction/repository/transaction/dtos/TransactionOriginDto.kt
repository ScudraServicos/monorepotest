package br.com.ume.application.shared.transaction.repository.transaction.dtos

import io.micronaut.core.annotation.Introspected
import org.hibernate.annotations.Generated
import org.hibernate.annotations.GenerationTime
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity(name = "TRANSACTION_ORIGIN")
@Table(name = "TRANSACTION_ORIGIN")
@Introspected
data class TransactionOriginDto(
    @Id
    @javax.persistence.GeneratedValue(
        strategy = GenerationType.IDENTITY,
        generator = "pg-uuid"
    )
    @GenericGenerator(
        name = "pg-uuid",
        strategy = "uuid2",
        parameters = [Parameter(
            name = "uuid_gen_strategy_class",
            value = "com.ume.application.shared.db.PostgreSQLUUIDGenerationStrategy"
        )]
    )
    val id: UUID? = null,

    @Column(name = "EXTERNAL_ID", columnDefinition = "SERIAL")
    @Generated(GenerationTime.INSERT)
    val externalId: Long? = null,

    @Column(name = "SOURCE_PRODUCT_REFERENCE_NAME", nullable = false)
    val sourceProductReferenceName: String,

    @Column(name = "SOURCE_PRODUCT_REFERENCE_ID", nullable = false)
    val sourceProductReferenceId: String,

    @Column(name = "USER_ID", nullable = true)
    val userId: String?,

    @Column(name = "CREATION_TIMESTAMP", nullable = false)
    val creationTimestamp: Timestamp,

    @Column(name = "UPDATE_TIMESTAMP", nullable = false)
    val updateTimestamp: Timestamp,
)