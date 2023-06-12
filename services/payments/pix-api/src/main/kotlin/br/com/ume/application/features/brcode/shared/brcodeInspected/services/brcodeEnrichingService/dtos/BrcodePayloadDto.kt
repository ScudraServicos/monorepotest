package br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeEnrichingService.dtos

data class BrcodePayloadDto(
    val revisao: Int,
    val calendario: BrCodePayloadCalendario,
    val devedor: BrCodePayloadDevedor?,
    val recebedor: BrCodePayloadRecebedor?,
    val valor: BrCodePayloadValor,
    val chave: String,
    val txid: String,
    val solicitacaoPagador: String?,
    val infoAdicionais: List<BrCodePayloadInfoAdicional>?,
    val status: String
)

data class BrCodePayloadCalendario(
    val criacao: String,
    val apresentacao: String,
    val expiracao: Int?,
    val dataDeVencimento: String?
) {}

data class BrCodePayloadDevedor(
    val cpf: String?,
    val cnpj: String?,
    val nome: String
) {}

data class BrCodePayloadRecebedor(
    val cpf: String?,
    val cnpj: String?,
    val nome: String,
    val nomeFantasia: String?
) {}

data class BrCodePayloadValor(
    val original: String,
    val modalidadeAlteracao: Int?, // 0 | 1
    val retirada: BrCodePayloadValorRetirada?
) {}

data class BrCodePayloadValorRetirada(
    val saque: BrCodePayloadValorRetiradaSaque?,
    val troco: BrCodePayloadValorRetiradaTroco?
) {}

data class BrCodePayloadValorRetiradaSaque(
    val valor: String,
    val modalidadeAlteracao: Int?, // 0 | 1
    val prestadorDoServicoDeSaque: String,
    val modalidadeAgente: String
) {}

data class BrCodePayloadValorRetiradaTroco(
    val valor: String,
    val modalidadeAlteracao: Int?, // 0 | 1
    val prestadorDoServicoDeSaque: String,
    val modalidadeAgente: String
) {}

data class BrCodePayloadInfoAdicional(
    val nome: String,
    val valor: String
) {}
