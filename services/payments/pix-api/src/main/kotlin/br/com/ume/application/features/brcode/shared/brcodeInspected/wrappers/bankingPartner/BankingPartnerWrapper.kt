package br.com.ume.application.features.brcode.shared.brcodeInspected.wrappers.bankingPartner

import br.com.ume.application.features.brcode.shared.brcodeInspected.wrappers.bankingPartner.dtos.BankingPartnerBrcodePreview
import com.starkbank.BrcodePayment
import com.starkbank.DictKey

interface BankingPartnerWrapper {
    fun getBrcodePreview(brcode: String): BankingPartnerBrcodePreview
    fun getPixBeneficiary(pixKey: String): DictKey
    fun payBrcode(brcode: String, beneficiaryDocument: String, tags: List<String>): BrcodePayment
    fun getBrcodePaymentByTag(tag: String): BrcodePayment?
}