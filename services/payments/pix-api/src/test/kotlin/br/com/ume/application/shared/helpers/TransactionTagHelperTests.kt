package br.com.ume.application.shared.helpers

import br.com.ume.application.shared.transaction.helpers.TransactionTagHelper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TransactionTagHelperTests {
    @Nested
    @DisplayName("getTagFromProduct()")
    inner class GetTagFromProduct {
        @Test
        fun `Should create tag from product`() {
            // Given
            val productId = "123"
            val sourceProduct = "pay-anywhere"

            // When
            val result = TransactionTagHelper.getTagFromProduct(productId, sourceProduct)

            // Then
            val expectedResult = "123_pay-anywhere"
            assertEquals(expectedResult, result)
        }

        @Test
        fun `Should create tag from empty product id`() {
            // Given
            val productId = ""
            val sourceProduct = "pay-anywhere"

            // When
            val result = TransactionTagHelper.getTagFromProduct(productId, sourceProduct)

            // Then
            val expectedResult = "_pay-anywhere"
            assertEquals(expectedResult, result)
        }

        @Test
        fun `Should create tag from empty source product`() {
            // Given
            val productId = "123"
            val sourceProduct = ""

            // When
            val result = TransactionTagHelper.getTagFromProduct(productId, sourceProduct)

            // Then
            val expectedResult = "123_"
            assertEquals(expectedResult, result)
        }
    }
}