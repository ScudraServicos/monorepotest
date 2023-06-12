package br.com.ume.api.controllers.transactions.transport.getTransaction.validators

import br.com.ume.api.controllers.transactions.transport.getTransaction.GetTransactionRequest
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class GetTransactionRequestValidatorTests {
    private lateinit var validator: GetTransactionRequestValidator
    private lateinit var annotationValueMock: AnnotationValue<ValidateGetTransactionRequest>
    private lateinit var constraintValidatorContextMock: ConstraintValidatorContext
    
    @BeforeEach
    fun setUp() {
        validator = GetTransactionRequestValidator()
        annotationValueMock = Mockito.mock(AnnotationValue::class.java) as AnnotationValue<ValidateGetTransactionRequest>
        constraintValidatorContextMock = Mockito.mock(ConstraintValidatorContext::class.java)
    }

    @Nested
    @DisplayName("isValid")
    inner class IsValid {
        @Test
        fun `Should be valid if transactionId exists`() {
            // Given
            val request = GetTransactionRequest(
                transactionId = "4c1ef6f9-3d1a-46e5-8222-b3d674aff767",
                sourceProductId = null,
                sourceProductName = null
            )

            // When
            val result = validator.isValid(request, annotationValueMock, constraintValidatorContextMock)

            // Then
            assertTrue(result)
        }

        @Test
        fun `Should be valid if product name and id exists`() {
            // Given
            val request = GetTransactionRequest(
                transactionId = null,
                sourceProductId = "id",
                sourceProductName = "name"
            )

            // When
            val result = validator.isValid(request, annotationValueMock, constraintValidatorContextMock)

            // Then
            assertTrue(result)
        }

        @Test
        fun `Should be invalid if missing product id`() {
            // Given
            val request = GetTransactionRequest(
                transactionId = null,
                sourceProductId = null,
                sourceProductName = "name"
            )

            // When
            val result = validator.isValid(request, annotationValueMock, constraintValidatorContextMock)

            // Then
            assertFalse(result)
        }

        @Test
        fun `Should be invalid if missing product name`() {
            // Given
            val request = GetTransactionRequest(
                transactionId = null,
                sourceProductId = "id",
                sourceProductName = null
            )

            // When
            val result = validator.isValid(request, annotationValueMock, constraintValidatorContextMock)

            // Then
            assertFalse(result)
        }

        @Test
        fun `Should be invalid if there's no product info or transaction id`() {
            // Given
            val request = GetTransactionRequest(
                transactionId = null,
                sourceProductId = null,
                sourceProductName = null
            )

            // When
            val result = validator.isValid(request, annotationValueMock, constraintValidatorContextMock)

            // Then
            assertFalse(result)
        }
    }
}