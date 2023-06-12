package br.com.ume.application.shared.services.coordinatorService

import br.com.ume.api.configs.CoordinatorConfigurations
import br.com.ume.application.shared.externalServices.coordinator.CoordinatorService
import br.com.ume.application.shared.externalServices.coordinator.CoordinatorServiceHttpImpl
import br.com.ume.application.shared.externalServices.coordinator.dtos.CancelContractBody
import br.com.ume.application.shared.testBuilders.ContractBuilder.Companion.buildContract
import br.com.ume.application.shared.testBuilders.ContractBuilder.Companion.buildProposal
import br.com.ume.application.shared.utils.CustomSerializer
import br.com.ume.application.shared.utils.http.HttpBuilder
import com.fasterxml.jackson.core.JsonParseException
import io.micronaut.http.HttpStatus
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.kotlin.eq
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class CoordinatorHttpServiceTests {

    companion object {
        private const val defaultContractId = "123"
        private const val defaultProposalId = "2"
        private const val defaultToken = "user.jwt.defaultHeaders"
        private const val defaultXForwardedFor = "192.168.0.1,192.168.0.2"
        private val defaultHeaders = mapOf("Authorization" to defaultToken, "X-Forwarded-For" to defaultXForwardedFor)
    }

    private lateinit var coordinatorConfigsMock: CoordinatorConfigurations
    private lateinit var httpBuilderMock: HttpBuilder
    private lateinit var httpClientMock: HttpClient
    private lateinit var coordinatorService: CoordinatorService
    private lateinit var httpResponseMock: HttpResponse<Any>

    @BeforeEach
    fun setUp() {
        coordinatorConfigsMock = Mockito.mock(CoordinatorConfigurations::class.java)
        Mockito.`when`(coordinatorConfigsMock.apiUrl).thenReturn("https://test.com")
        Mockito.`when`(coordinatorConfigsMock.apiKey).thenReturn(defaultToken)
        httpClientMock = Mockito.mock(HttpClient::class.java)
        httpResponseMock = Mockito.mock(HttpResponse::class.java) as HttpResponse<Any>
        httpBuilderMock = Mockito.mock(HttpBuilder::class.java)
        Mockito.`when`(httpBuilderMock.buildClient(10)).thenReturn(httpClientMock)
        coordinatorService = CoordinatorServiceHttpImpl(coordinatorConfigsMock, httpBuilderMock)
    }

    @Nested
    @DisplayName("getContract()")
    inner class GetContract {

        @Test
        fun `Should get the contract successfully`() {
            val borrowerId = "1"
            val contract = buildContract(
                defaultContractId, borrowerId, listOf(buildProposal("2", defaultContractId))
            )
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.OK.code)
            Mockito.`when`(httpResponseMock.body()).thenReturn(
                CustomSerializer.serialize(contract)
            )
            Mockito.`when`(
                httpClientMock.send<Any>(
                    eq(buildGetContractRequest(defaultContractId, defaultHeaders, coordinatorConfigsMock.apiUrl!!)),
                    any()
                )
            ).thenReturn(httpResponseMock)

            val result = coordinatorService.getContract(defaultContractId, defaultHeaders)

            assertEquals(contract, result)

            Mockito.verify(httpResponseMock, Mockito.times(1)).statusCode()
            Mockito.verify(httpResponseMock, Mockito.times(1)).body()
            Mockito.verify(httpClientMock, Mockito.times(1)).send<Any>(
                eq(buildGetContractRequest(defaultContractId, defaultHeaders, coordinatorConfigsMock.apiUrl!!)),
                any()
            )
        }

        @Test
        fun `Should fail when send request fail`() {
            Mockito.`when`(
                httpClientMock.send<Any>(
                    eq(buildGetContractRequest(defaultContractId, defaultHeaders, coordinatorConfigsMock.apiUrl!!)),
                    any()
                )
            ).thenThrow(RuntimeException("ERROR"))
            val exception =
                assertThrows<RuntimeException> { coordinatorService.getContract(defaultContractId, defaultHeaders) }

            assertEquals("ERROR", exception.message)

            Mockito.verify(httpClientMock, Mockito.times(1)).send<Any>(
                eq(buildGetContractRequest(defaultContractId, defaultHeaders, coordinatorConfigsMock.apiUrl!!)),
                any()
            )
        }

        @Test
        fun `Should return null is http response code is not a success code`() {
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.code)
            Mockito.`when`(
                httpClientMock.send<Any>(
                    eq(buildGetContractRequest(defaultContractId, defaultHeaders, coordinatorConfigsMock.apiUrl!!)),
                    any()
                )
            ).thenReturn(httpResponseMock)

            val result = coordinatorService.getContract(defaultContractId, defaultHeaders)

            assertNull(result)

            Mockito.verify(httpResponseMock, Mockito.times(1)).statusCode()
            Mockito.verify(httpClientMock, Mockito.times(1)).send<Any>(
                eq(buildGetContractRequest(defaultContractId, defaultHeaders, coordinatorConfigsMock.apiUrl!!)),
                any()
            )
        }

        @Test
        fun `Should fail when http response deserialization fail`() {
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.OK.code)
            Mockito.`when`(httpResponseMock.body()).thenReturn("wrong body format")
            Mockito.`when`(
                httpClientMock.send<Any>(
                    eq(buildGetContractRequest(defaultContractId, defaultHeaders, coordinatorConfigsMock.apiUrl!!)),
                    any()
                )
            ).thenReturn(httpResponseMock)

            assertThrows<JsonParseException> { coordinatorService.getContract(defaultContractId, defaultHeaders) }

            Mockito.verify(httpResponseMock, Mockito.times(1)).statusCode()
            Mockito.verify(httpResponseMock, Mockito.times(1)).body()
            Mockito.verify(httpClientMock, Mockito.times(1)).send<Any>(
                eq(buildGetContractRequest(defaultContractId, defaultHeaders, coordinatorConfigsMock.apiUrl!!)),
                any()
            )
        }

        private fun buildGetContractRequest(
            contractId: String,
            headers: Map<String, String>,
            apiUrl: String
        ): HttpRequest {
            val requestBuilder = HttpRequest.newBuilder()
                .GET()
                .uri(URI("$apiUrl/contracts/$contractId?allowUnacceptedContract=true"))
            headers.forEach { header ->
                requestBuilder.setHeader(header.key, header.value)
            }
            return requestBuilder.build()
        }

    }

    @Nested
    @DisplayName("acceptProposal()")
    inner class AcceptProposal {

        @Test
        fun `Should accept the proposal successfully`() {
            val contractId = "123"
            val borrowerId = "1"
            val contract = buildContract(
                contractId, borrowerId, listOf(buildProposal(defaultProposalId, contractId, wasAccepted = true))
            )
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.OK.code)
            Mockito.`when`(httpResponseMock.body()).thenReturn(
                CustomSerializer.serialize(contract)
            )
            Mockito.`when`(
                httpClientMock.send<Any>(
                    eq(buildAcceptProposalRequest(defaultProposalId, defaultHeaders, coordinatorConfigsMock.apiUrl!!)),
                    any()
                )
            ).thenReturn(httpResponseMock)

            val result = coordinatorService.acceptProposal(defaultProposalId, defaultHeaders)

            assertEquals(contract, result)

            Mockito.verify(httpResponseMock, Mockito.times(1)).statusCode()
            Mockito.verify(httpResponseMock, Mockito.times(1)).body()
            Mockito.verify(httpClientMock, Mockito.times(1)).send<Any>(
                eq(buildAcceptProposalRequest(defaultProposalId, defaultHeaders, coordinatorConfigsMock.apiUrl!!)),
                any()
            )
        }

        @Test
        fun `Should fail when send request fail`() {
            Mockito.`when`(
                httpClientMock.send<Any>(
                    eq(buildAcceptProposalRequest(defaultProposalId, defaultHeaders, coordinatorConfigsMock.apiUrl!!)),
                    any()
                )
            ).thenThrow(RuntimeException("ERROR"))

            val exception =
                assertThrows<RuntimeException> { coordinatorService.acceptProposal(defaultProposalId, defaultHeaders) }

            assertEquals("ERROR", exception.message)

            Mockito.verify(httpClientMock, Mockito.times(1)).send<Any>(
                eq(buildAcceptProposalRequest(defaultProposalId, defaultHeaders, coordinatorConfigsMock.apiUrl!!)),
                any()
            )
        }

        @Test
        fun `Should return null is http response code is not a success code`() {
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.code)
            Mockito.`when`(
                httpClientMock.send<Any>(
                    eq(buildAcceptProposalRequest(defaultProposalId, defaultHeaders, coordinatorConfigsMock.apiUrl!!)),
                    any()
                )
            ).thenReturn(httpResponseMock)

            val result = coordinatorService.acceptProposal(defaultProposalId, defaultHeaders)

            assertNull(result)

            Mockito.verify(httpResponseMock, Mockito.times(1)).statusCode()
            Mockito.verify(httpClientMock, Mockito.times(1)).send<Any>(
                eq(buildAcceptProposalRequest(defaultProposalId, defaultHeaders, coordinatorConfigsMock.apiUrl!!)),
                any()
            )
        }

        @Test
        fun `Should fail when http response deserialization fail`() {
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.OK.code)
            Mockito.`when`(httpResponseMock.body()).thenReturn("wrong body format")
            Mockito.`when`(
                httpClientMock.send<Any>(
                    eq(buildAcceptProposalRequest(defaultProposalId, defaultHeaders, coordinatorConfigsMock.apiUrl!!)),
                    any()
                )
            ).thenReturn(httpResponseMock)

            assertThrows<JsonParseException> { coordinatorService.acceptProposal(defaultProposalId, defaultHeaders) }

            Mockito.verify(httpResponseMock, Mockito.times(1)).statusCode()
            Mockito.verify(httpResponseMock, Mockito.times(1)).body()
            Mockito.verify(httpClientMock, Mockito.times(1)).send<Any>(
                eq(buildAcceptProposalRequest(defaultProposalId, defaultHeaders, coordinatorConfigsMock.apiUrl!!)),
                any()
            )
        }

        private fun buildAcceptProposalRequest(
            proposalId: String,
            headers: Map<String, String>,
            apiUrl: String
        ): HttpRequest {
            val requestBuilder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(URI("$apiUrl/contracts/proposals/$proposalId/accept"))
            headers.forEach { header ->
                requestBuilder.setHeader(header.key, header.value)
            }
            return requestBuilder.build()
        }
    }

    @Nested
    @DisplayName("cancelContract()")
    inner class CancelContract {
        private val defaultHeaders = mapOf("Authorization" to defaultToken, "Content-Type" to "application/json")
        private val defaultContractId = "123"
        private val defaultReason = "I want it that way"
        private val defaultBody = CancelContractBody(
            reason = defaultReason,
            requester = "PAY_ANYWHERE"
        )

        @Test
        fun `Should cancel the contract successfully`() {
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.OK.code)
            Mockito.`when`(
                httpClientMock.send<Any>(eq(buildCancelContractRequest(defaultContractId, defaultBody)), any())
            ).thenReturn(httpResponseMock)

            val result = coordinatorService.cancelContract(defaultContractId, defaultReason)

            assertTrue(result)

            Mockito.verify(httpResponseMock, Mockito.times(1)).statusCode()
            Mockito.verify(httpClientMock, Mockito.times(1)).send<Any>(
                eq(buildCancelContractRequest(defaultContractId, defaultBody)),
                any()
            )
        }

        @Test
        fun `Should fail when send request fail`() {
            Mockito.`when`(
                httpClientMock.send<Any>(eq(buildCancelContractRequest(defaultContractId, defaultBody)), any())
            ).thenThrow(RuntimeException("ERROR"))

            val exception =
                assertThrows<RuntimeException> { coordinatorService.cancelContract(defaultContractId, defaultReason) }

            assertEquals("ERROR", exception.message)

            Mockito.verify(httpClientMock, Mockito.times(1)).send<Any>(
                eq(buildCancelContractRequest(defaultContractId, defaultBody)), any()
            )
        }

        @Test
        fun `Should return false if http response code is not a success code`() {
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.code)
            Mockito.`when`(
                httpClientMock.send<Any>(eq(buildCancelContractRequest(defaultContractId, defaultBody)), any())
            ).thenReturn(httpResponseMock)

            val result = coordinatorService.cancelContract(defaultContractId, defaultReason)

            assertFalse(result)

            Mockito.verify(httpResponseMock, Mockito.times(1)).statusCode()
            Mockito.verify(httpClientMock, Mockito.times(1)).send<Any>(
                eq(buildCancelContractRequest(defaultContractId, defaultBody)), any()
            )
        }

        private fun buildCancelContractRequest(
            contractId: String,
            body: CancelContractBody
        ): HttpRequest {
            val baseUrl = coordinatorConfigsMock.apiUrl!!
            val requestBuilder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(CustomSerializer.serialize(body)))
                .uri(URI("$baseUrl/contracts/$contractId/cancel"))
            defaultHeaders.forEach { header ->
                requestBuilder.setHeader(header.key, header.value)
            }
            return requestBuilder.build()
        }
    }
}