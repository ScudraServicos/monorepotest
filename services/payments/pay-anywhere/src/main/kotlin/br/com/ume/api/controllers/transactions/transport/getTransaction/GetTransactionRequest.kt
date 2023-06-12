package br.com.ume.api.controllers.transactions.transport.getTransaction

import io.micronaut.core.annotation.Introspected
import io.micronaut.http.annotation.QueryValue
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
data class GetTransactionRequest(
    @field:QueryValue @field:NotNull @field:NotBlank
    val contractId: String,

    @field:QueryValue @field:NotNull @field:NotBlank
    val userId: String
)