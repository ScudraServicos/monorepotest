package br.com.ume.application.transaction.testBuilders

import br.com.ume.application.shared.payment.repository.dtos.PaymentDto
import br.com.ume.application.shared.payment.repository.dtos.PaymentOriginDto
import java.sql.Timestamp
import java.util.*

class PaymentDtoBuilder {
    companion object {
        fun build(): PaymentDto {
            val timestamp = Timestamp(1L)
            return PaymentDto(
                paymentOrigin = PaymentOriginDto(
                    id = UUID.randomUUID(),
                    userId = "1",
                    contractId = "1",
                    creationTimestamp = timestamp,
                    updateTimestamp = timestamp
                ),
                id = UUID.randomUUID(),
                value = 20.0,
                brCode = "brCode",
                creationTimestamp = timestamp,
                updateTimestamp = timestamp
            )
        }
    }
}