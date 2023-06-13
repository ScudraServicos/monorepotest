package br.com.ume.api.controllers.brcode

import br.com.ume.api.exceptions.base.ApiError
import br.com.ume.application.features.brcode.inspectBrcode.useCase.InspectBrcodeUseCaseImpl
import br.com.ume.application.features.brcode.inspectBrcode.useCase.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.exceptions.BrcodeValidationException
import br.com.ume.application.shared.testBuilders.InspectedBrcodeBuilder
import com.google.gson.Gson
import io.micronaut.http.HttpStatus
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.restassured.specification.RequestSpecification
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*

@MicronautTest()
class BrcodeControllerTests {
    @Inject
    lateinit var inspectBrcodeUseCase: InspectBrcodeUseCaseImpl

    companion object {
        private const val defaultUserId = "userId"
    }

    @Nested
    @DisplayName("/brcode/inspect")
    inner class InspectBrcode {
        @Test
        fun `Should correctly return brcode details`(spec: RequestSpecification) {
            // Given
            val brcode = "123.321"
            Mockito.`when`(inspectBrcodeUseCase.execute(brcode, defaultUserId))
                .thenReturn(InspectedBrcodeBuilder.buildDynamic())
            // When
            val response = spec
                .given()
                .header("X-API-KEY", "123")
                .`when`()
                .get("/api/brcode/inspect?brcode=$brcode&userId=$defaultUserId")

            // Then
            Mockito.verify(inspectBrcodeUseCase, Mockito.times(1)).execute(brcode, defaultUserId)
            assertEquals(200, response.statusCode)
        }

        @Test
        fun `Should return bad request for validation errors`(spec: RequestSpecification) {
            // Given
            val brcode = "123.321"
            Mockito.`when`(inspectBrcodeUseCase.execute(brcode, defaultUserId)).thenAnswer {
                throw BrcodeValidationException(InspectBrcodeErrorEnum.NATURAL_PERSON_BENEFICIARY)
            }

            // When
            val response = spec
                .given()
                .header("X-API-KEY", "123")
                .`when`()
                .get("/api/brcode/inspect?brcode=$brcode&userId=$defaultUserId")

            // Then
            Mockito.verify(inspectBrcodeUseCase, Mockito.times(1)).execute(brcode, defaultUserId)
            assertEquals(HttpStatus.BAD_REQUEST.code, response.statusCode)
            val responseBody = Gson().fromJson(response.body.asString(), ApiError::class.java)
            assertEquals(InspectBrcodeErrorEnum.NATURAL_PERSON_BENEFICIARY.toString(), responseBody.message)
        }
    }

    @MockBean(InspectBrcodeUseCaseImpl::class)
    fun mockedInspectBrcodeUseCase() = Mockito.mock(InspectBrcodeUseCaseImpl::class.java)
}