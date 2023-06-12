package br.com.ume.application.features.brcode.shared.services.pixApiService

import br.com.ume.api.configs.PixApiConfigurations
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.*
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.transactions.PixApiGetTransactionOutput
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.transactions.PixApiTransactionResponseDto
import br.com.ume.application.features.brcode.shared.services.pixApiService.enums.PixApiInspectErrorEnum
import br.com.ume.application.features.brcode.shared.services.pixApiService.enums.PixApiTransactionErrorEnum
import br.com.ume.application.features.brcode.shared.services.pPixApiPaymentRequestDtoixApiService.dtos.PixApiPaymentRequestDto
import br.com.ume.application.shared.externalServices.coordinator.enums.SourceProductEnum
import br.com.ume.libs.logging.gcp.JsonLogBuilder
import br.com.ume.libs.logging.gcp.LoggerFactory
import br.com.ume.application.shared.utils.CustomDeserializer
import br.com.ume.application.shared.utils.CustomSerializer
import br.com.ume.application.shared.utils.http.isSuccessHttpCode
import io.micronaut.http.HttpStatus
import io.micronaut.runtime.http.scope.RequestScope
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.util.logging.Logger

@RequestScope
class PixApiServiceImpl(
    private val pixApiConfig: PixApiConfigurations,
    private val httpClient: PixApiServiceHttpClient
) : PixApiService {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(PixApiServiceImpl::class.java.name)
    }

    override fun inspectBrcode(brcode: String): PixApiInspectOutput {
        val encodedBrcode = URLEncoder.encode(brcode, StandardCharsets.UTF_8.toString())

        val request = HttpRequest.newBuilder()
            .uri(URI("${pixApiConfig.apiUrl}/brcode/inspect?brcode=$encodedBrcode"))
            .setHeader("X-API-KEY", pixApiConfig.apiKey)
            .build()

        val response = httpClient.client.send(request, HttpResponse.BodyHandlers.ofString())

        val statusCode = response.statusCode()
        if (!isSuccessHttpCode(statusCode)) {
            log.severe(JsonLogBuilder.build(object {
                val message = "Error while trying to inspect brcode"
                val statusCode = statusCode
                val body = response.body()
            }))
            val responseError = parseBodyResponseError(response.body())
            val error = parseInspectResponseErrorMessage(responseError)
            return PixApiInspectOutput(error = error)
        }

        val inspectedBrcode = CustomDeserializer.deserialize(response.body(), PixApiInspectResponseDto::class.java)
        return PixApiInspectOutput(inspectedBrcode)
    }

    override fun payBrcode(brcode: String): PixApiPaymentOutput {
        val requestBody = CustomSerializer.serialize(PixApiPaymentRequestDto(brcode))

        val request = HttpRequest.newBuilder()
            .uri(URI("${pixApiConfig.apiUrl}/brcode/pay"))
            .setHeader("Content-Type", "application/json")
            .setHeader("X-API-KEY", pixApiConfig.apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()

        val response = httpClient.client.send(request, HttpResponse.BodyHandlers.ofString())

        val statusCode = response.statusCode()
        if (!isSuccessHttpCode(statusCode)) {
            log.severe(JsonLogBuilder.build(object {
                val message = "PAYMENT_ERROR"
                val statusCode = statusCode
                val body = response.body()
            }))
            return PixApiPaymentOutput(error = "PAYMENT_ERROR")
        }

        val payment = CustomDeserializer.deserialize(response.body(), PixApiPaymentResponseDto::class.java)

        return PixApiPaymentOutput(payment)
    }

    override fun getTransaction(sourceProductId: String): PixApiGetTransactionOutput {
        val request = HttpRequest.newBuilder()
            .uri(URI("${pixApiConfig.apiUrl}/transactions?sourceProductName=${SourceProductEnum.PAY_ANYWHERE}&sourceProductId=${sourceProductId}"))
            .setHeader("X-API-KEY", pixApiConfig.apiKey)
            .build()

        val response = httpClient.client.send(request, HttpResponse.BodyHandlers.ofString())

        val statusCode = response.statusCode()
        if (!isSuccessHttpCode(statusCode)) {
            log.severe(JsonLogBuilder.build(object {
                val message = "Error while trying to getTransaction transaction"
                val statusCode = statusCode
                val body = response.body()
            }))
            if (response.statusCode() == HttpStatus.NOT_FOUND.code) {
                return PixApiGetTransactionOutput(error = PixApiTransactionErrorEnum.TRANSACTION_NOT_FOUND)
            }
            return PixApiGetTransactionOutput(error = PixApiTransactionErrorEnum.UNKNOWN_ERROR)
        }

        val transaction = CustomDeserializer.deserialize(response.body(), PixApiTransactionResponseDto::class.java)
        return PixApiGetTransactionOutput(transaction)
    }

    private fun parseBodyResponseError(body: String): PixApiErrorResponseDto {
        return try {
            CustomDeserializer.deserialize(body, PixApiErrorResponseDto::class.java)
        } catch (ex: Exception) {
            log.severe(JsonLogBuilder.build(object {
                val message = "Error parsing pix api error"
                val body = body
            }))
            PixApiErrorResponseDto(PixApiInspectErrorEnum.UNKNOWN_ERROR.toString())
        }
    }

    private fun parseInspectResponseErrorMessage(responseError: PixApiErrorResponseDto): PixApiInspectErrorEnum {
        return try {
            PixApiInspectErrorEnum.valueOf(responseError.message)
        } catch (ex: IllegalArgumentException) {
            PixApiInspectErrorEnum.UNKNOWN_ERROR
        }
    }
}