package br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeEnrichingService

import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.helpers.BrcodePayloadHelper
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeEnrichingService.dtos.GetBrcodePayloadOutput
import br.com.ume.application.shared.logging.gcp.JsonLogBuilder
import br.com.ume.application.shared.logging.gcp.LoggerFactory
import io.micronaut.http.HttpStatus
import io.micronaut.runtime.http.scope.RequestScope
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.logging.Logger

@RequestScope
class BrcodeEnrichingServiceImpl(
    private val httpClient: BrcodeEnrichingServiceHttpClient
) : BrcodeEnrichingService {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(BrcodeEnrichingServiceImpl::class.java.name)
    }

    override fun getBrcodePayload(merchantUrl: String): GetBrcodePayloadOutput {
        val request = HttpRequest.newBuilder()
            .uri(URI("https://$merchantUrl"))
            .build();

        val response = httpClient.client.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() == HttpStatus.NOT_FOUND.code || response.statusCode() == HttpStatus.GONE.code) {
            return GetBrcodePayloadOutput(error = InspectBrcodeErrorEnum.BRCODE_PAYLOAD_NOT_AVAILABLE)
        } else if (response.statusCode() == HttpStatus.BAD_REQUEST.code) {
            return GetBrcodePayloadOutput(error = InspectBrcodeErrorEnum.BRCODE_PAYLOAD_NOT_VALID)
        } else if (response.statusCode() >= 500) {
            log.severe(JsonLogBuilder.build(object {
                val message = InspectBrcodeErrorEnum.BRCODE_PAYLOAD_NOT_VALID.toString()
                val body = response.body()
            }))
            return GetBrcodePayloadOutput(error = InspectBrcodeErrorEnum.BRCODE_PAYLOAD_ERROR)
        }

        val responseToken: String = response.body()
        val brcodePayload = BrcodePayloadHelper.decodePayloadToken(responseToken)
            ?: return GetBrcodePayloadOutput(error = InspectBrcodeErrorEnum.BRCODE_PAYLOAD_DECODING_ERROR)

        return GetBrcodePayloadOutput(value = brcodePayload)
    }
}