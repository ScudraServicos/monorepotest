package br.com.ume.application.accessControl.getAccessControl.useCase

import br.com.ume.api.exceptions.types.ForbiddenException
import br.com.ume.application.features.accessControl.getAccessControl.useCase.GetAccessControlUseCaseImpl
import br.com.ume.application.shared.accessControl.domain.AccessControl
import br.com.ume.application.shared.accessControl.gateway.AccessControlGateway
import org.junit.jupiter.api.*
import org.mockito.Mockito
import org.junit.jupiter.api.Assertions

class GetAccessControlUseCaseTests {
    private lateinit var accessControlGateway: AccessControlGateway
    private lateinit var getAccessControlUseCase: GetAccessControlUseCaseImpl

    @BeforeEach
    fun setUp() {
        accessControlGateway = Mockito.mock(AccessControlGateway::class.java)
        getAccessControlUseCase = GetAccessControlUseCaseImpl(accessControlGateway)
    }

    @Nested
    @DisplayName("execute()")
    inner class GetAccessControlUseCaseTest {
        @Test
        fun `Should not throw ending request exception when user is allowed`() {
            // Given
            val userId = "123"
            val accessControl = AccessControl(userId = userId, allowed = true, groups = setOf("G1"))
            Mockito.`when`(accessControlGateway.getAccessControl(userId)).thenReturn(accessControl)

            // When
            getAccessControlUseCase.execute(userId)

            // Then
            Mockito.verify(accessControlGateway, Mockito.times(1)).getAccessControl(userId)
        }

        @Test
        fun `Should throw forbidden when user not allowed`() {
            // Given
            val userId = "123"
            val accessControl = AccessControl(userId = userId, allowed = false, groups = setOf("G1", "G2"))
            Mockito.`when`(accessControlGateway.getAccessControl(userId)).thenReturn(accessControl)

            // When - Then
            Assertions.assertThrows(
                ForbiddenException::class.java
            ) { getAccessControlUseCase.execute(userId) }
            Mockito.verify(accessControlGateway, Mockito.times(1)).getAccessControl(userId)
        }

        @Test
        fun `Should throw forbidden when could not get user access control`() {
            // Given
            val userId = "123"
            val accessControl = AccessControl(userId = userId, allowed = true, groups = emptySet())
            Mockito.`when`(accessControlGateway.getAccessControl(userId)).thenReturn(null)

            // When - Then
            Assertions.assertThrows(
                ForbiddenException::class.java
            ) { getAccessControlUseCase.execute(userId) }
            Mockito.verify(accessControlGateway, Mockito.times(1)).getAccessControl(userId)
        }
    }
}