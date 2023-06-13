package br.com.ume.application.shared.services.pixApiService

import br.com.ume.api.configs.PixApiConfigurations
import br.com.ume.api.transformObjectFromSerializer
import br.com.ume.application.features.brcode.shared.services.pixApiService.PixApiService
import br.com.ume.application.features.brcode.shared.services.pixApiService.PixApiServiceImpl
import br.com.ume.application.features.brcode.shared.services.pixApiService.PixApiServiceHttpClient
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.PixApiErrorResponseDto
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.PixApiInspectOutput
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.PixApiPaymentOutput
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.PixApiPaymentResponseDto
import br.com.ume.application.features.brcode.shared.services.pixApiService.dtos.transactions.PixApiGetTransactionOutput
import br.com.ume.application.features.brcode.shared.services.pixApiService.enums.PixApiInspectErrorEnum
import br.com.ume.application.features.brcode.shared.services.pixApiService.enums.PixApiTransactionErrorEnum
import br.com.ume.application.shared.enums.PixPaymentStatusEnum
import br.com.ume.application.shared.testBuilders.PixApiInspectResponseBuilder
import br.com.ume.application.shared.testBuilders.pixApi.transactions.PixApiGetTransactionResponseBuilder
import br.com.ume.application.shared.utils.CustomSerializer
import io.micronaut.http.HttpStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import java.net.http.HttpClient
import java.net.http.HttpResponse

class PixApiServiceImplTests {
    private lateinit var pixApiConfigMock: PixApiConfigurations
    private lateinit var pixApiHttpClientMock: PixApiServiceHttpClient
    private lateinit var httpClientMock: HttpClient
    private lateinit var pixApiService: PixApiService
    private lateinit var httpResponseMock: HttpResponse<Any>

    @BeforeEach
    fun setUp() {
        pixApiConfigMock = Mockito.mock(PixApiConfigurations::class.java)
        Mockito.`when`(pixApiConfigMock.apiUrl).thenReturn("https://test.com")
        Mockito.`when`(pixApiConfigMock.apiKey).thenReturn("123")
        pixApiHttpClientMock = Mockito.mock(PixApiServiceHttpClient::class.java)
        httpClientMock = Mockito.mock(HttpClient::class.java)
        httpResponseMock = Mockito.mock(HttpResponse::class.java) as HttpResponse<Any>
        Mockito.`when`(pixApiHttpClientMock.client).thenReturn(httpClientMock)
        Mockito.`when`(httpClientMock.send<Any>(any(), any())).thenReturn(httpResponseMock)

        pixApiService = PixApiServiceImpl(pixApiConfigMock, pixApiHttpClientMock)
    }

    @Nested
    @DisplayName("inspectBrcode()")
    inner class InspectBrcode {
        @Test
        fun `Should return inspected brcode`() {
            // Given
            val brcode = "some_brcode"
            val pixApiResponse = PixApiInspectResponseBuilder.buildDynamic()
            Mockito.`when`(httpResponseMock.body()).thenReturn(
                CustomSerializer.serialize(pixApiResponse)
            )
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.OK.code)

            // When
            val result = pixApiService.inspectBrcode(brcode)

            // Then
            assertEquals(PixApiInspectOutput(pixApiResponse), result)
        }

        @Test
        fun `Should return output error on pix api 400 error`() {
            // Given
            val brcode = "some_brcode"
            val pixApiResponse = PixApiErrorResponseDto(message = PixApiInspectErrorEnum.BANKING_PARTNER_INVALID_QR_CODE.toString())
            Mockito.`when`(httpResponseMock.body()).thenReturn(
                CustomSerializer.serialize(pixApiResponse)
            )
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.BAD_REQUEST.code)

            // When
            val result = pixApiService.inspectBrcode(brcode)

            // Then
            assertEquals(PixApiInspectOutput(error = PixApiInspectErrorEnum.BANKING_PARTNER_INVALID_QR_CODE), result)
        }

        @Test
        fun `Should return output error on pix api 500 error`() {
            // Given
            val brcode = "some_brcode"
            val pixApiResponse = PixApiErrorResponseDto(message = PixApiInspectErrorEnum.BANKING_PARTNER_ERROR.toString())
            Mockito.`when`(httpResponseMock.body()).thenReturn(
                CustomSerializer.serialize(pixApiResponse)
            )
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.code)

            // When
            val result = pixApiService.inspectBrcode(brcode)

            // Then
            assertEquals(PixApiInspectOutput(error = PixApiInspectErrorEnum.BANKING_PARTNER_ERROR), result)
        }

        @Test
        fun `Should return UNKNOWN_ERROR if pix api error response can't be parsed`() {
            // Given
            val brcode = "some_brcode"
            val pixApiResponse = hashMapOf("invalidAttribute" to "whatever")
            Mockito.`when`(httpResponseMock.body()).thenReturn(
                CustomSerializer.serialize(pixApiResponse)
            )
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.BAD_REQUEST.code)

            // When
            val result = pixApiService.inspectBrcode(brcode)

            // Then
            assertEquals(PixApiInspectOutput(error = PixApiInspectErrorEnum.UNKNOWN_ERROR), result)
        }

        @Test
        fun `Should return UNKNOWN_ERROR if pix api error response message is not mapped`() {
            // Given
            val brcode = "some_brcode"
            val pixApiResponse = PixApiErrorResponseDto(message = "UNMAPPED_ERROR")
            Mockito.`when`(httpResponseMock.body()).thenReturn(
                CustomSerializer.serialize(pixApiResponse)
            )
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.BAD_REQUEST.code)

            // When
            val result = pixApiService.inspectBrcode(brcode)

            // Then
            assertEquals(PixApiInspectOutput(error = PixApiInspectErrorEnum.UNKNOWN_ERROR), result)
        }
    }

    @Nested
    @DisplayName("payBrcode()")
    inner class PayBrcode {
        @Test
        fun `Should return payment`() {
            // Given
            val brcode = "123.321"
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.OK.code)
            Mockito.`when`(httpResponseMock.body()).thenReturn(
                CustomSerializer.serialize(PixApiPaymentResponseDto(PixPaymentStatusEnum.CREATED))
            )

            // When
            val result = pixApiService.payBrcode(brcode)

            // Then
            val expectedResult = PixApiPaymentOutput(
                PixApiPaymentResponseDto(PixPaymentStatusEnum.CREATED)
            )
            assertEquals(expectedResult, result)
        }

        @Test
        fun `Should return output error on pix api 500 error`() {
            // Given
            val brcode = "123.321"
            val pixApiResponse = PixApiErrorResponseDto(message = "ERROR")
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.code)
            Mockito.`when`(httpResponseMock.body()).thenReturn(
                CustomSerializer.serialize(pixApiResponse)
            )

            // When
            val result = pixApiService.payBrcode(brcode)

            // Then
            val expectedResult = PixApiPaymentOutput(error = "PAYMENT_ERROR")
            assertEquals(expectedResult, result)
        }
    }

    @Nested
    @DisplayName("getTransaction()")
    inner class GetTransaction {
        @Test
        fun `Should return transaction`() {
            // Given
            val contractId = "1"
            val getTransactionResponse = PixApiGetTransactionResponseBuilder.build()
            Mockito.`when`(httpResponseMock.body()).thenReturn(
                CustomSerializer.serialize(getTransactionResponse)
            )
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.OK.code)

            // When
            val result = pixApiService.getTransaction(contractId)

            // Then
            assertEquals(transformObjectFromSerializer(PixApiGetTransactionOutput(getTransactionResponse)), result)
        }

        @Test
        fun `Should return output error on pix api 404 error`() {
            // Given
            val contractId = "1"
            val pixApiResponse = PixApiErrorResponseDto(message = PixApiTransactionErrorEnum.TRANSACTION_NOT_FOUND.toString())
            Mockito.`when`(httpResponseMock.body()).thenReturn(
                CustomSerializer.serialize(pixApiResponse)
            )
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.NOT_FOUND.code)

            // When
            val result = pixApiService.getTransaction(contractId)

            // Then
            assertEquals(PixApiGetTransactionOutput(error = PixApiTransactionErrorEnum.TRANSACTION_NOT_FOUND), result)
        }

        @Test
        fun `Should return output error on any other pix api error`() {
            // Given
            val contractId = "1"
            val pixApiResponse = PixApiErrorResponseDto(message = PixApiTransactionErrorEnum.UNKNOWN_ERROR.toString())
            Mockito.`when`(httpResponseMock.body()).thenReturn(
                CustomSerializer.serialize(pixApiResponse)
            )
            Mockito.`when`(httpResponseMock.statusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.code)

            // When
            val result = pixApiService.getTransaction(contractId)

            // Then
            assertEquals(PixApiGetTransactionOutput(error = PixApiTransactionErrorEnum.UNKNOWN_ERROR), result)
        }
    }
}