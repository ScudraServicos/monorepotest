package br.com.ume.application.shared.transaction.repository.transaction.dtos

import org.hibernate.annotations.Generated
import org.hibernate.annotations.Parameter
import org.hibernate.annotations.GenerationTime
import org.hibernate.annotations.GenericGenerator
import io.micronaut.core.annotation.Introspected
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity(name = "TRANSACTION")
@Table(name = "TRANSACTION")
@Introspected
data class TransactionDto(

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
            value = "com.ume.application.shared.db.PostgreSQLUUIDGenerationStrategy"
        )]
    )
    val id: UUID? = null,

    @Column(name = "EXTERNAL_ID", columnDefinition = "serial")
    @Generated(GenerationTime.INSERT)
    val externalId: Long? = null,

    @Column(name = "VALUE", nullable = false)
    val value: Double,

    @Column(name = "TYPE", nullable = false)
    val type: String,

    @Column(name = "PARTNER_EXTERNAL_ID", nullable = true)
    val partnerExternalId: String? = null,

    @Column(name = "PIX_TXID")
    val txId: String?,

    @Column(name = "BRCODE")
    val brcode: String?,

    @Column(name = "STATUS", nullable = false)
    val status: String,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "TRANSACTION_BENEFICIARY_ID")
    val beneficiary: TransactionBeneficiaryDto,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "TRANSACTION_ORIGIN_ID")
    val origin: TransactionOriginDto,

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "transaction")
    val refunds: List<TransactionRefundDto> = emptyList(),

    @Column(name = "CREATION_TIMESTAMP", nullable = false)
    val creationTimestamp: Timestamp,

    @Column(name = "UPDATE_TIMESTAMP", nullable = false)
    val updateTimestamp: Timestamp,
)
