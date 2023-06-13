package br.com.ume.api.controllers.brcode.dtos

import io.micronaut.core.annotation.Introspected
import io.micronaut.http.annotation.QueryValue
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
data class InspectBrCodeRequest(
    @field:QueryValue @field:NotBlank @field:NotNull
    val userId: String,

    @field:QueryValue @field:NotBlank @field:NotNull
    val brcode: String,
)