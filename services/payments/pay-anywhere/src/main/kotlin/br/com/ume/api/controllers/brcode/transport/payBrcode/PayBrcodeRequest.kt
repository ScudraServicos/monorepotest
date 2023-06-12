package br.com.ume.api.controllers.brcode.transport.payBrcode

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
data class PayBrcodeRequest(
    @field: NotBlank @field: NotNull
    val brcode: String,

    @field: NotBlank @field: NotNull
    val userId: String
)
