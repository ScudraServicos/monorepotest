package br.com.ume.application.brcode.shared.services

import br.com.ume.application.brcode.shared.testBuilders.BrcodePayloadBuilder
import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeEnrichingService.BrcodeEnrichingServiceImpl
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeEnrichingService.BrcodeEnrichingServiceHttpClient
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeEnrichingService.BrcodeEnrichingService
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeEnrichingService.dtos.GetBrcodePayloadOutput
import io.micronaut.http.HttpStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import java.net.http.HttpClient
import java.net.http.HttpResponse

class BrcodeEnrichingServiceImplTests {
    private lateinit var httpClientMock: HttpClient
    private lateinit var brcodeEnrichingServiceHttpClient: BrcodeEnrichingServiceHttpClient
    private lateinit var httpResponseMock: HttpResponse<Any>
    private lateinit var brcodeEnrichingService: BrcodeEnrichingService

    @BeforeEach
    fun setUp() {
        httpClientMock = Mockito.mock(HttpClient::class.java)
        brcodeEnrichingServiceHttpClient = Mockito.mock(BrcodeEnrichingServiceHttpClient::class.java)
        Mockito.`when`(brcodeEnrichingServiceHttpClient.client).thenReturn(httpClientMock)
        httpResponseMock = Mockito.mock(HttpResponse::class.java) as HttpResponse<Any>
        Mockito.`when`(httpClientMock.send<Any>(any(), any())).thenReturn(httpResponseMock)

        brcodeEnrichingService = BrcodeEnrichingServiceImpl(brcodeEnrichingServiceHttpClient)
    }

    @Nested
    @DisplayName("getBrcodePayload()")
    inner class GetBrcodePayload {
        @Test
        fun `Should return brcode payload result`() {
            // Given
            val brcodePayload = BrcodePayloadBuilder.build()
            val brcodeEncodedPayload = BrcodePayloadBuilder.buildEncodedPayloadToken(brcodePayload)
            Mockito.`when`(httpResponseMock.body()).thenReturn(brcodeEncodedPayload)
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.OK.code)

            // When
            val result = brcodeEnrichingService.getBrcodePayload("abc.com/xyz")

            // Then
            assertEquals(GetBrcodePayloadOutput(value = brcodePayload), result)
        }

        @Test
        fun `Should return error when payload is not found`() {
            // Given
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.NOT_FOUND.code)

            // When
            val result = brcodeEnrichingService.getBrcodePayload("abc.com/xyz")

            // Then
            assertEquals(GetBrcodePayloadOutput(error = InspectBrcodeErrorEnum.BRCODE_PAYLOAD_NOT_AVAILABLE), result)
        }

        @Test
        fun `Should return error when payload is gone`() {
            // Given
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.GONE.code)

            // When
            val result = brcodeEnrichingService.getBrcodePayload("abc.com/xyz")

            // Then
            assertEquals(GetBrcodePayloadOutput(error = InspectBrcodeErrorEnum.BRCODE_PAYLOAD_NOT_AVAILABLE), result)
        }

        @Test
        fun `Should return error when payload is invalid`() {
            // Given
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.BAD_REQUEST.code)

            // When
            val result = brcodeEnrichingService.getBrcodePayload("abc.com/xyz")

            // Then
            assertEquals(
                GetBrcodePayloadOutput(error = InspectBrcodeErrorEnum.BRCODE_PAYLOAD_NOT_VALID),
                result
            )
        }

        @Test
        fun `Should return error when payload decoding fails`() {
            // Given
            Mockito.`when`(httpResponseMock.body()).thenReturn("123.321")
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.OK.code)

            // When
            val result = brcodeEnrichingService.getBrcodePayload("abc.com/xyz")

            // Then
            assertEquals(
                GetBrcodePayloadOutput(error = InspectBrcodeErrorEnum.BRCODE_PAYLOAD_DECODING_ERROR),
                result
            )
        }

        @Test
        fun `Should return error when payload returns an error`() {
            // Given
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.code)

            // When
            val result = brcodeEnrichingService.getBrcodePayload("abc.com/xyz")

            // Then
            assertEquals(
                GetBrcodePayloadOutput(error = InspectBrcodeErrorEnum.BRCODE_PAYLOAD_ERROR),
                result
            )
        }
    }
}