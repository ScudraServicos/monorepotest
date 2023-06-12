package br.com.ume.application.features.brcode.shared.services.pixApiService

import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.PixApiInspectOutput
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.PixApiPaymentOutput
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.transactions.PixApiGetTransactionOutput

interface PixApiService {
    fun inspectBrcode(brcode: String): PixApiInspectOutput
    fun payBrcode(brcode: String): PixApiPaymentOutput
    fun getTransaction(sourceProductId: String): PixApiGetTransactionOutput
}