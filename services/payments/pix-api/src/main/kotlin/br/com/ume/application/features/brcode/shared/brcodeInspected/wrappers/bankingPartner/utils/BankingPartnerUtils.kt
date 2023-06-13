package br.com.ume.application.features.brcode.shared.brcodeInspected.wrappers.bankingPartner.utils

fun parsePartnerAmount(amount: Number): Double {
    return amount.toDouble() / 100
}