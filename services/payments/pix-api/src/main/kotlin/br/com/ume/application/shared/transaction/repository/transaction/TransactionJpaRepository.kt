package br.com.ume.application.shared.transaction.repository.transaction

import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionDto
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaSpecificationExecutor
import io.micronaut.data.repository.CrudRepository
import java.sql.Timestamp
import java.util.*
import javax.transaction.Transactional

@Repository
@Transactional
interface TransactionJpaRepository : CrudRepository<TransactionDto, UUID>, JpaSpecificationExecutor<TransactionDto> {

    fun findByPartnerExternalId(partnerExternalId: String): Optional<TransactionDto>

    @Query("UPDATE TRANSACTION SET STATUS = :status, UPDATE_TIMESTAMP = :timestamp WHERE PARTNER_EXTERNAL_ID = :partnerExternalId")
    fun updateStatus(partnerExternalId: String, status: String, timestamp: Timestamp): Int

    fun findByOriginSourceProductReferenceNameAndOriginSourceProductReferenceId(
        sourceProductReferenceName: String,
        sourceProductReferenceId: String
    ): Optional<TransactionDto>

    @Query("UPDATE TRANSACTION SET STATUS = :status, PARTNER_EXTERNAL_ID = :partnerExternalId, UPDATE_TIMESTAMP = :timestamp WHERE id = :id")
    fun update(id: UUID, status: String, partnerExternalId: String, timestamp: Timestamp): Int

    fun update(@Id id: UUID, value: Double, updateTimestamp: Timestamp): Int
    fun update(@Id id: UUID, value: Double, status: String, updateTimestamp: Timestamp): Int
}