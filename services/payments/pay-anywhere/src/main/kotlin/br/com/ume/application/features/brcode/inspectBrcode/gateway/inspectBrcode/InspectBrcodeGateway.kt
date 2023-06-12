package br.com.ume.application.features.brcode.inspectBrcode.gateway.inspectBrcode

import br.com.ume.application.features.brcode.inspectBrcode.gateway.inspectBrcode.dtos.InspectBrcodeGatewayOutput

interface InspectBrcodeGateway {
    fun inspect(brcode: String, userId: String): InspectBrcodeGatewayOutput
}