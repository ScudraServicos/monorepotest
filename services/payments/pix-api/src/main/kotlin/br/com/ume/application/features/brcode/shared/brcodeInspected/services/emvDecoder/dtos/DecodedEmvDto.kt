package br.com.ume.application.features.brcode.shared.brcodeInspected.services.emvDecoder.dtos

data class DecodedEmvDto(
    val merchantUrl: String?,
    val pixKey: String?,
)
