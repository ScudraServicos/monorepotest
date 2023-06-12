package br.com.ume.application.shared.transaction.repository.transaction

import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionDto
import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionRefundDto
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaSpecificationExecutor
import io.micronaut.data.repository.CrudRepository
import java.sql.Timestamp
import java.util.*
import javax.transaction.Transactional

@Repository
@Transactional
interface TransactionRefundJpaRepository : CrudRepository<TransactionRefundDto, UUID>, JpaSpecificationExecutor<TransactionRefundDto> {}