package br.com.ume.application.shared.transaction.repository.transaction.dtos

import io.micronaut.core.annotation.Introspected
import org.hibernate.annotations.Generated
import org.hibernate.annotations.GenerationTime
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity(name = "TRANSACTION_REFUND")
@Table(name = "TRANSACTION_REFUND")
@Introspected
data class TransactionRefundDto(
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSACTION_ID", referencedColumnName = "ID")
    val transaction: TransactionDto,

    @Column(name = "TYPE", nullable = false)
    val type: String,

    @Column(name = "ORIGINAL_VALUE", nullable = false)
    val originalValue: Double,

    @Column(name = "PREVIOUS_VALUE", nullable = false)
    val previousValue: Double,

    @Column(name = "CURRENT_VALUE", nullable = false)
    val currentValue: Double,

    @Column(name = "CREATION_TIMESTAMP", nullable = false)
    val creationTimestamp: Timestamp,

    @Column(name = "UPDATE_TIMESTAMP", nullable = false)
    val updateTimestamp: Timestamp,
)