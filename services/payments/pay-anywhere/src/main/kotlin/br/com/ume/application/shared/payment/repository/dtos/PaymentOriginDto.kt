package br.com.ume.application.shared.payment.repository.dtos

import io.micronaut.core.annotation.Introspected
import org.hibernate.annotations.Generated
import org.hibernate.annotations.GenerationTime
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import java.sql.Timestamp
import java.util.UUID
import javax.persistence.*

@Entity(name = "PAYMENT_ORIGIN")
@Table(name = "PAYMENT_ORIGIN")
@Introspected
data class PaymentOriginDto(
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY,
        generator = "pg-uuid"
    )
    @GenericGenerator(
        name = "pg-uuid",
        strategy = "uuid2",
        parameters = [Parameter(
            name = "uuid_gen_strategy_class",
            value = "br.com.ume.application.shared.db.PostgreSQLUUIDGenerationStrategy"
        )]
    )
    val id: UUID? = null,

    @Column(name = "EXTERNAL_ID", columnDefinition = "SERIAL")
    @Generated(GenerationTime.INSERT)
    val externalId: Long? = null,

    @Column(name = "USER_ID", nullable = false)
    val userId: String,

    @Column(name = "CONTRACT_ID", nullable = false)
    val contractId: String,

    @Column(name = "CREATION_TIMESTAMP", nullable = false)
    val creationTimestamp: Timestamp,

    @Column(name = "UPDATE_TIMESTAMP", nullable = false)
    val updateTimestamp: Timestamp,
)


