package br.com.ume.application.shared.utils.bankingPartner

fun parseValueToPartnerAmount(value: Double): Long {
    return (value * 100).toLong()
}