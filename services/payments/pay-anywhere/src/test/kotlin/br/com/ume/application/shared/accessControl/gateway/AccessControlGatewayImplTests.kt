package br.com.ume.application.shared.accessControl.gateway

import br.com.ume.application.shared.accessControl.domain.AccessControl
import br.com.ume.application.shared.accessControl.repository.AccessControlRepositoryImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class AccessControlGatewayImplTests {
    private lateinit var accessControlRepository: AccessControlRepositoryImpl
    private lateinit var accessControlGateway: AccessControlGatewayImpl

    @BeforeEach
    fun setUp() {
        accessControlRepository = Mockito.mock(AccessControlRepositoryImpl::class.java)
        accessControlGateway = AccessControlGatewayImpl(accessControlRepository)
    }

    @Nested
    @DisplayName("getAccessControl()")
    inner class GetAccessControl {
        @Test
        fun `Should return access control information`() {
            // Given
            val userId = "123"
            val accessControl = AccessControl(userId = userId, emptySet(), allowed = true)
            Mockito.`when`(accessControlRepository.getAccessControl(userId)).thenReturn(accessControl)

            // When
            val result = accessControlGateway.getAccessControl(userId)

            // Then
            Mockito.verify(accessControlRepository, Mockito.times(1)).getAccessControl(userId)
            assertEquals(accessControl, result)
        }

        @Test
        fun `Should return null when could not gather access control for user`() {
            // Given
            val userId = "123"
            Mockito.`when`(accessControlRepository.getAccessControl(userId)).thenReturn(null)

            // When
            val result = accessControlGateway.getAccessControl(userId)

            // Then
            Mockito.verify(accessControlRepository, Mockito.times(1)).getAccessControl(userId)
            assertNull(result)
        }
    }
}