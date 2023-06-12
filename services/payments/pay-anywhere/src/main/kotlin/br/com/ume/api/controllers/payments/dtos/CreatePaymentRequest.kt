package br.com.ume.api.controllers.payments.dtos

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
data class CreatePaymentRequest(
    @field:NotBlank @field:NotNull
    val proposalId: String,

    @field:NotBlank @field:NotNull
    val contractId: String,

    @field:NotBlank @field:NotNull
    val brCode: String,

    @field:NotBlank @field:NotNull
    val userId: String,
)