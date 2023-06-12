package br.com.ume.application.shared.accessControl.repository

import br.com.ume.application.shared.accessControl.domain.AccessControl
import br.com.ume.application.shared.accessControl.repository.dtos.AccessControlDto
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import org.mockito.exceptions.base.MockitoException
import org.mockito.internal.matchers.apachecommons.ReflectionEquals
import java.time.Instant
import java.util.*

class AccessControlRepositoryImplTests {
    private lateinit var accessControlJpaRepository: AccessControlJpaRepository
    private lateinit var accessControlRepository: AccessControlRepositoryImpl

    @BeforeEach
    fun setUp() {
        accessControlJpaRepository = Mockito.mock(AccessControlJpaRepository::class.java)
        accessControlRepository = AccessControlRepositoryImpl(accessControlJpaRepository)
    }

    @Nested
    @DisplayName("getAccessControl()")
    inner class GetAccessControl {
        @Test
        fun `Should correctly return access control`() {
            // Given
            val userId = "123"
            val accessControlDto = AccessControlDto(
                userId = userId,
                allowed = true,
                creationDate = Date.from(Instant.now()),
                alterationDate = Date.from(Instant.now()),
                groups = "G1,G2"
            )
            val accessControl = AccessControl(accessControlDto)
            Mockito.`when`(accessControlJpaRepository.findById(userId)).thenReturn(Optional.of(accessControlDto))

            // When
            val result = accessControlRepository.getAccessControl(userId)

            // Then
            Mockito.verify(accessControlJpaRepository, Mockito.times(1)).findById(userId)
            assertTrue(ReflectionEquals(accessControl).matches(result))
        }

        @Test
        fun `Should correctly return user now allowed when user not found`() {
            // Given
            val userId = "123"
            val accessControl = AccessControl(userId = userId, emptySet(), allowed = false)
            Mockito.`when`(accessControlJpaRepository.findById(userId)).thenReturn(Optional.empty())

            // When
            val result = accessControlRepository.getAccessControl(userId)

            // Then
            Mockito.verify(accessControlJpaRepository, Mockito.times(1)).findById(userId)
            assertTrue(ReflectionEquals(accessControl).matches(result))
        }

        @Test
        fun `Should correctly return null when could not get access control`() {
            // Given
            val userId = "123"
            Mockito.`when`(accessControlJpaRepository.findById(userId)).thenThrow(MockitoException::class.java)

            // When
            val result = accessControlRepository.getAccessControl(userId)

            // Then
            Mockito.verify(accessControlJpaRepository, Mockito.times(1)).findById(userId)
            assertNull(result)
        }
    }
}