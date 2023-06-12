package br.com.ume.application.features.brcode.payBrcode.useCase

import br.com.ume.application.features.brcode.payBrcode.useCase.dtos.TransactionOriginDto
import br.com.ume.application.shared.transaction.domain.Transaction

interface PayBrcodeUseCase {
    fun execute(brcode: String, transactionOrigin: TransactionOriginDto): Transaction
}