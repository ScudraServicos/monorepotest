package br.com.ume.api.controllers.accessControl

import br.com.ume.application.features.accessControl.getAccessControl.useCase.GetAccessControlUseCaseImpl
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.restassured.specification.RequestSpecification
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@MicronautTest()
class AccessControlControllerTests {
    @Inject
    lateinit var getAccessControlUseCase: GetAccessControlUseCaseImpl

    @Nested
    @DisplayName("/access-control/{userId}")
    inner class GetAccessControl {
        @Test
        fun `Should correctly return user access control`(spec: RequestSpecification) {
            // Given
            val userId = "123"

            // When
            val response = spec
                .given()
                .header("X-API-KEY", "123")
                .`when`()
                .get("/api/access-control/$userId")

            // Then
            Mockito.verify(getAccessControlUseCase, Mockito.times(1)).execute(userId)
            Assertions.assertEquals(200, response.statusCode)
        }
    }

    @MockBean(GetAccessControlUseCaseImpl::class)
    fun mockedGetAccessControlUseCase() = Mockito.mock(GetAccessControlUseCaseImpl::class.java)
}