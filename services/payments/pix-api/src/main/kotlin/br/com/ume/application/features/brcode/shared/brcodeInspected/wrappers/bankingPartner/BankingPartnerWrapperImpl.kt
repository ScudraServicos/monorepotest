package br.com.ume.application.features.brcode.shared.brcodeInspected.wrappers.bankingPartner

import br.com.ume.application.features.brcode.shared.brcodeInspected.wrappers.bankingPartner.dtos.BankingPartnerBrcodePreview
import br.com.ume.application.utils.CustomDeserializer
import br.com.ume.application.utils.CustomSerializer
import com.starkbank.BrcodePayment
import com.starkbank.DictKey
import com.starkbank.PaymentPreview
import io.micronaut.runtime.http.scope.RequestScope

// TODO: Add unit testing
@RequestScope
class BankingPartnerWrapperImpl: BankingPartnerWrapper {
    override fun getBrcodePreview(brcode: String): BankingPartnerBrcodePreview {
        val previews = PaymentPreview.create(
            listOf(PaymentPreview(hashMapOf<String, Any>("id" to brcode)))
        ) as MutableList<PaymentPreview>

        val serialized = CustomSerializer.serialize(previews.first().payment)
        return CustomDeserializer.deserialize(serialized, BankingPartnerBrcodePreview::class.java)
    }

    override fun getPixBeneficiary(pixKey: String): DictKey {
        return DictKey.get(pixKey)
    }

    override fun payBrcode(brcode: String, beneficiaryDocument: String, tags: List<String>): BrcodePayment {
        val data: HashMap<String, Any> = HashMap()
        data["brcode"] = brcode
        data["taxId"] = beneficiaryDocument
        data["description"] = "Pago com a UME"
        if (tags.isNotEmpty()) data["tags"] = tags.toTypedArray()

        val createdPayments = BrcodePayment.create(arrayListOf(BrcodePayment(data)))
        return createdPayments.first()
    }

    override fun getBrcodePaymentByTag(tag: String): BrcodePayment? {
        val params: HashMap<String, Any> = hashMapOf("tags" to tag)

        val result = BrcodePayment.query(params)
        return result.firstOrNull()
    }
}