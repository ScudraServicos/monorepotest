package br.com.ume.application.brcode.shared.testBuilders

import com.starkbank.BrcodePayment
import java.time.LocalDateTime


abstract class BankingPartnerBrcodePaymentBuilder {
    companion object {
        fun build(): BrcodePayment {
            val rules: MutableList<BrcodePayment.Rule> = ArrayList()
            rules.add(BrcodePayment.Rule("resendingLimit", 5))

            return BrcodePayment(
                "00020126580014br.gov.bcb.pix013635719950-ac93-4bab-8ad6-56d7fb63afd252040000530398654040.005802BR5915Stark Bank S.A.6009Sao Paulo62070503***6304AA26",
                "20.018.183/0001-80",
                "Tony Stark's Suit",
                1765,
                LocalDateTime.now().toString(),
                "Tony",
                arrayOf("Stark", "Suit"),
                rules,
                "123123",
                "success",
                "dynamic",
                arrayOf(),
                10,
                LocalDateTime.now().toString(),
                LocalDateTime.now().toString(),
            )
        }
    }
}