package br.com.ume.application.shared.transaction.repository.transaction.dtos

import io.micronaut.core.annotation.Introspected
import org.hibernate.annotations.Generated
import org.hibernate.annotations.GenerationTime
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity(name = "TRANSACTION_BENEFICIARY")
@Table(name = "TRANSACTION_BENEFICIARY")
@Introspected
data class TransactionBeneficiaryDto(

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

    @Column(name = "EXTERNAL_ID", columnDefinition = "SERIAL")
    @Generated(GenerationTime.INSERT)
    val externalId: Long? = null,

    @Column(name = "NAME", nullable = false)
    val name: String,

    @Column(name = "LEGAL_NATURE", nullable = false)
    val legalNature: String,

    @Column(name = "DOCUMENT", nullable = false)
    val document: String,

    @Column(name = "PIX_KEY", nullable = false)
    val pixKey: String,

    @Column(name = "BANK_ISPB_CODE", nullable = false)
    val bankIspbCode: String,

    @Column(name = "BANK_BRANCH", nullable = false)
    val bankBranch: String,

    @Column(name = "BANK_ACCOUNT", nullable = false)
    val bankAccount: String,

    @Column(name = "CREATION_TIMESTAMP", nullable = false)
    val creationTimestamp: Timestamp,

    @Column(name = "UPDATE_TIMESTAMP", nullable = false)
    val updateTimestamp: Timestamp,

    @Column(name = "BANK_NAME", nullable = false)
    val bankName: String,

    @Column(name = "ACCOUNT_TYPE", nullable = false)
    val accountType: String,
)