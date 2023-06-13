package br.com.ume.application.features.transaction.getTransaction.useCase

import br.com.ume.api.exceptions.types.NotFoundException
import br.com.ume.application.features.transaction.getTransaction.enums.GetTransactionErrorEnum
import br.com.ume.application.shared.transaction.domain.Transaction
import br.com.ume.application.shared.transaction.gateway.TransactionGateway
import br.com.ume.application.shared.transaction.repository.transaction.filter.TransactionFilter
import io.micronaut.runtime.http.scope.RequestScope

@RequestScope
class GetTransactionUseCaseImpl(
    private val transactionGateway: TransactionGateway
) : GetTransactionUseCase {
    override fun execute(input: GetTransactionUseCaseInput): Transaction {
        return transactionGateway.getTransactions(
            TransactionFilter(
                transactionId = input.transactionId,
                sourceProductName = input.sourceProductName,
                sourceProductId = input.sourceProductId
            )
        ).firstOrNull() ?: throw NotFoundException(GetTransactionErrorEnum.TRANSACTION_NOT_FOUND.toString())
    }
}