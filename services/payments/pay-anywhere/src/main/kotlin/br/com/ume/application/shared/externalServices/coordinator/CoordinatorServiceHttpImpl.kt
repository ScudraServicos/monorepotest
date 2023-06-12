package br.com.ume.application.shared.externalServices.coordinator

import br.com.ume.api.configs.CoordinatorConfigurations
import br.com.ume.application.shared.externalServices.coordinator.dtos.CancelContractBody
import br.com.ume.application.shared.externalServices.coordinator.dtos.Contract
import br.com.ume.libs.logging.gcp.JsonLogBuilder
import br.com.ume.libs.logging.gcp.LoggerFactory
import br.com.ume.application.shared.utils.CustomDeserializer
import br.com.ume.application.shared.utils.CustomSerializer
import br.com.ume.application.shared.utils.http.HttpBuilder
import br.com.ume.application.shared.utils.http.isSuccessHttpCode
import io.micronaut.runtime.http.scope.RequestScope
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse
import java.util.logging.Logger

@RequestScope
class CoordinatorServiceHttpImpl(
    private val coordinatorConfigs: CoordinatorConfigurations,
    httpBuilder: HttpBuilder,
) : CoordinatorService {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(CoordinatorServiceHttpImpl::class.java.name)
    }
    private val httpClient: HttpClient = httpBuilder.buildClient(10)

    override fun getContract(contractId: String, headers: Map<String, String>): Contract? {
        val requestBuilder = HttpRequest.newBuilder()
            .GET()
            .uri(URI("${coordinatorConfigs.apiUrl}/contracts/$contractId?allowUnacceptedContract=true"))
        headers.forEach { header ->
            requestBuilder.setHeader(header.key, header.value)
        }
        val response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())

        val statusCode = response.statusCode()
        if (!isSuccessHttpCode(statusCode)) {
            log.severe(JsonLogBuilder.build(object {
                val message = "Error while trying to get contract"
                val statusCode = statusCode
                val contractId = contractId
                val body = response.body()
            }))
            return null
        }

        return CustomDeserializer.deserialize(response.body(), Contract::class.java)
    }

    override fun acceptProposal(proposalId: String, headers: Map<String, String>): Contract? {
        val requestBuilder = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.noBody())
            .uri(URI("${coordinatorConfigs.apiUrl}/contracts/proposals/$proposalId/accept"))
        headers.forEach { header ->
            requestBuilder.setHeader(header.key, header.value)
        }
        val response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())

        val statusCode = response.statusCode()
        if (!isSuccessHttpCode(statusCode)) {
            log.severe(JsonLogBuilder.build(object {
                val message = "Error while trying to accept proposal"
                val statusCode = statusCode
                val proposalId = proposalId
                val body = response.body()
            }))
            return null
        }

        return CustomDeserializer.deserialize(response.body(), Contract::class.java)
    }

    override fun cancelContract(contractId: String, reason: String): Boolean {
        val body = CancelContractBody(
            reason = reason,
            requester = "PAY_ANYWHERE"
        )
        val requestBuilder = HttpRequest.newBuilder()
            .uri(URI("${coordinatorConfigs.apiUrl}/contracts/$contractId/cancel"))
            .setHeader("Authorization", coordinatorConfigs.apiKey)
            .setHeader("Content-Type", "application/json")
            .POST(BodyPublishers.ofString(CustomSerializer.serialize(body)))
        val response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())

        val statusCode = response.statusCode()
        if (!isSuccessHttpCode(statusCode)) {
            log.severe(JsonLogBuilder.build(object {
                val message = "Error while trying to cancel contract"
                val contractId = contractId
                val statusCode = statusCode
                val body = response.body()
            }))
            return false
        }
        return true
    }
}