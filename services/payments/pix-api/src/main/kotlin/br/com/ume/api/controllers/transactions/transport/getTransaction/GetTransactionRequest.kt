package br.com.ume.api.controllers.transactions.transport.getTransaction

import br.com.ume.api.controllers.transactions.transport.getTransaction.validators.ValidateGetTransactionRequest
import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.Nullable
import io.micronaut.http.annotation.QueryValue

@Introspected
@ValidateGetTransactionRequest
data class GetTransactionRequest(
    @field:QueryValue @field:Nullable
    val transactionId: String?,

    @field:QueryValue @field:Nullable
    val sourceProductId: String?,

    @field:QueryValue @field:Nullable
    val sourceProductName: String?
)