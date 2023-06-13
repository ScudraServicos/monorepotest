package br.com.ume.application.shared.payment.repository.dtos

import io.micronaut.core.annotation.Introspected
import org.hibernate.annotations.Generated
import org.hibernate.annotations.GenerationTime
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity(name = "PAYMENT")
@Table(name = "PAYMENT")
@Introspected
data class PaymentDto(
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

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "PAYMENT_ORIGIN_ID")
    val paymentOrigin: PaymentOriginDto,

    @Column(name = "VALUE", nullable = false)
    val value: Double,

    @Column(name = "BR_CODE", nullable = false)
    val brCode: String,

    @Column(name = "CREATION_TIMESTAMP", nullable = false)
    val creationTimestamp: Timestamp,

    @Column(name = "UPDATE_TIMESTAMP", nullable = false)
    val updateTimestamp: Timestamp,
)
