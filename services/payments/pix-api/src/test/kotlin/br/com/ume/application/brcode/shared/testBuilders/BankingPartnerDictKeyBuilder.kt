package br.com.ume.application.brcode.shared.testBuilders

import com.starkbank.DictKey

abstract class BankingPartnerDictKeyBuilder {
    companion object {
        fun build(): DictKey {
            return DictKey(
                "1aa1aaaa-a11a-1111-a111-1a1aa111aaaa",
                "evp",
                "2020-11-09T21:53:03.691460+00:00",
                "saving",
                "Red Forman",
                "***.456.789-**",
                "naturalPerson",
                "Stark Bank",
                "93641612",
                "4506",
                "2924145735530665",
                "registered",
                "2020-11-09T21:53:03.691460+00:00",
                "2020-11-09T21:53:03.691460+00:00",
            )
        }
    }
}