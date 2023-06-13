package br.com.ume.api.controllers.brcode.transport.payBrcode

import br.com.ume.application.features.brcode.payBrcode.enums.BrcodePaymentStatus

data class PayBrcodeResponse(
    val transactionId: String,
    val status: BrcodePaymentStatus
)
