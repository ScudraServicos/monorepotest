package br.com.ume.application.brcode.shared.testBuilders

import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeEnrichingService.dtos.*
import com.google.gson.Gson
import java.util.*

abstract class BrcodePayloadBuilder {
    companion object {
        fun build(): BrcodePayloadDto {
            return BrcodePayloadDto(
                revisao = 0,
                calendario = BrCodePayloadCalendario(
                    criacao = "2023-01-30T14:59:15",
                    apresentacao = "2023-01-30T14:59:45",
                    expiracao = null,
                    dataDeVencimento = "2023-02-15"
                ),
                devedor = BrCodePayloadDevedor(
                    cpf = "776.414.130-56",
                    cnpj = null,
                    nome = "Donna Pinciotti"
                ),
                recebedor = BrCodePayloadRecebedor(
                    cpf = null,
                    cnpj = "18.164.062/0001-02",
                    nome = "The Hub",
                    nomeFantasia = "The Hub"
                ),
                valor = BrCodePayloadValor(
                    original = "25.00",
                    modalidadeAlteracao = 0,
                    retirada = null
                ),
                chave = "776.414.130-56",
                txid = "123-312",
                solicitacaoPagador = null,
                infoAdicionais = listOf(
                    BrCodePayloadInfoAdicional(
                    nome = "color",
                    valor = "none"
                )
                ),
                status = "ATIVA",
            )
        }

        fun buildWithTroco(): BrcodePayloadDto {
            return build().copy(
                valor = BrCodePayloadValor(
                    original = "25.00",
                    modalidadeAlteracao = 0,
                    retirada = BrCodePayloadValorRetirada(
                        saque = null,
                        troco = BrCodePayloadValorRetiradaTroco(
                            valor = "10.0",
                            modalidadeAlteracao = 0,
                            prestadorDoServicoDeSaque = "123",
                            modalidadeAgente = "Don't know"
                        )
                    )
                )
            )
        }

        fun buildWithSaque(): BrcodePayloadDto {
            return build().copy(
                valor = BrCodePayloadValor(
                    original = "25.00",
                    modalidadeAlteracao = 0,
                    retirada = BrCodePayloadValorRetirada(
                        troco = null,
                        saque = BrCodePayloadValorRetiradaSaque(
                            valor = "15.0",
                            modalidadeAlteracao = 0,
                            prestadorDoServicoDeSaque = "321",
                            modalidadeAgente = "Dunno"
                        )
                    )
                )
            )
        }

        fun buildEncodedPayloadToken(brcodePayload: BrcodePayloadDto): String {
            val jsonBrcodePayload = Gson().toJson(brcodePayload)
            val encodedBrcodePayload = Base64.getEncoder().encodeToString(jsonBrcodePayload.toByteArray())
            return "abc.$encodedBrcodePayload.123"
        }
    }
}