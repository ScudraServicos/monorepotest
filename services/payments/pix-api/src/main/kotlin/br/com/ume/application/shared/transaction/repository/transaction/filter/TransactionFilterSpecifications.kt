package br.com.ume.application.shared.transaction.repository.transaction.filter

import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionDto
import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionOriginDto
import io.micronaut.data.jpa.repository.criteria.Specification
import java.util.UUID
import javax.persistence.criteria.Predicate

fun transactionsQuerySpecification(transactionFilter: TransactionFilter): Specification<TransactionDto> {
    return Specification { root, _, criteriaBuilder ->
        val predicates = mutableListOf<Predicate>()

        transactionFilter.transactionId?.let {
            val id = root.get<UUID>(TransactionDto::id::name.get())
            predicates.add(criteriaBuilder.equal(id, UUID.fromString(transactionFilter.transactionId)))
        }

        transactionFilter.sourceProductName?.let {
            val productName = root.get<TransactionOriginDto>(
                TransactionDto::origin::name.get()
            ).get<String>(TransactionOriginDto::sourceProductReferenceName::name.get())
            predicates.add(criteriaBuilder.equal(productName, transactionFilter.sourceProductName))
        }

        transactionFilter.sourceProductId?.let {
            val productId = root.get<TransactionOriginDto>(
                TransactionDto::origin::name.get()
            ).get<String>(TransactionOriginDto::sourceProductReferenceId::name.get())
            predicates.add(criteriaBuilder.equal(productId, transactionFilter.sourceProductId))
        }

        criteriaBuilder.and(*predicates.toTypedArray())
    }
}