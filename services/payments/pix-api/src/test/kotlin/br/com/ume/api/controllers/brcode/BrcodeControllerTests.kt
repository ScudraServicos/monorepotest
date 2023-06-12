package br.com.ume.api.controllers.brcode

import br.com.ume.application.brcode.shared.testBuilders.BrcodeInspectedBuilder
import br.com.ume.application.features.brcode.inspectBrcode.useCase.InspectBrcodeUseCaseImpl
import br.com.ume.application.features.brcode.payBrcode.useCase.PayBrcodeUseCaseImpl
import br.com.ume.application.features.brcode.shared.brcodeInspected.domain.BrcodeInspected
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.restassured.specification.RequestSpecification
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.times

@MicronautTest
class BrcodeControllerTests {
    @Inject
    lateinit var inspectBrcodeUseCase: InspectBrcodeUseCaseImpl
    @Inject
    lateinit var payBrcodeUseCase: PayBrcodeUseCaseImpl

    @Nested
    @DisplayName("GET /inspect")
    inner class InspectBrcode {
        @Test
        fun `Should return inspected brcode`(spec: RequestSpecification) {
            // Given
            val brcodeInspected = BrcodeInspectedBuilder.buildStatic()
            Mockito.`when`(inspectBrcodeUseCase.execute("abc123xyz"))
                .thenReturn(brcodeInspected)

            // When
            val response = spec
                .given()
                .header("X-API-KEY", "123")
                .`when`()
                .get("/api/brcode/inspect?brcode=abc123xyz")

            val inspectedBrcodeResponse = jacksonObjectMapper().readValue(response.body.asString(), BrcodeInspected::class.java)

            // Then
            Mockito.verify(inspectBrcodeUseCase, times(1)).execute(anyString())
            Assertions.assertEquals(200, response.statusCode)
            Assertions.assertEquals(brcodeInspected, inspectedBrcodeResponse)
        }
    }

    @MockBean(InspectBrcodeUseCaseImpl::class)
    fun mockedInspectBrcodeUseCase() = Mockito.mock(InspectBrcodeUseCaseImpl::class.java)
}