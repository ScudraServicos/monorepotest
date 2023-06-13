package br.com.ume.application.transaction.updateTransactionStatus.utils

import br.com.ume.application.features.transaction.updateTransactionStatus.useCase.utils.isStatusUpdatable
import br.com.ume.application.shared.transaction.enums.TransactionStatusEnum
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UpdateTransactionStatusUtilsTests {
    @Nested
    @DisplayName("isStatusUpdatable()")
    inner class IsStatusUpdatable {
        private fun assertUpdatableStatus(
            currentStatus: TransactionStatusEnum,
            statusUpdatableMap: HashMap<TransactionStatusEnum, Boolean>
        ) {
            assertEquals(statusUpdatableMap.size, TransactionStatusEnum.values().size)
            for ((status, isUpdatable) in statusUpdatableMap) {
                val result = isStatusUpdatable(currentStatus, status)
                assertEquals(result, isUpdatable)
            }
        }

        @Test
        fun `Should handle updatable status for PENDING_CREATION`() {
            // Given
            val currentStatus = TransactionStatusEnum.PENDING_CREATION
            val statusUpdatableMap = hashMapOf(
                TransactionStatusEnum.PENDING_CREATION to false,
                TransactionStatusEnum.CREATED to true,
                TransactionStatusEnum.PROCESSING to true,
                TransactionStatusEnum.CANCELED to true,
                TransactionStatusEnum.FAILED to true,
                TransactionStatusEnum.REFUNDED to true,
                TransactionStatusEnum.SUCCESS to true
            )

            // When / Then
            assertUpdatableStatus(currentStatus, statusUpdatableMap)
        }

        @Test
        fun `Should handle updatable status for CREATED`() {
            // Given
            val currentStatus = TransactionStatusEnum.CREATED
            val statusUpdatableMap = hashMapOf(
                TransactionStatusEnum.PENDING_CREATION to false,
                TransactionStatusEnum.CREATED to false,
                TransactionStatusEnum.PROCESSING to true,
                TransactionStatusEnum.CANCELED to true,
                TransactionStatusEnum.FAILED to true,
                TransactionStatusEnum.REFUNDED to true,
                TransactionStatusEnum.SUCCESS to true
            )

            // When / Then
            assertUpdatableStatus(currentStatus, statusUpdatableMap)
        }

        @Test
        fun `Should handle updatable status for PROCESSING`() {
            // Given
            val currentStatus = TransactionStatusEnum.PROCESSING
            val statusUpdatableMap = hashMapOf(
                TransactionStatusEnum.PENDING_CREATION to false,
                TransactionStatusEnum.CREATED to false,
                TransactionStatusEnum.PROCESSING to false,
                TransactionStatusEnum.CANCELED to true,
                TransactionStatusEnum.FAILED to true,
                TransactionStatusEnum.REFUNDED to true,
                TransactionStatusEnum.SUCCESS to true
            )

            // When / Then
            assertUpdatableStatus(currentStatus, statusUpdatableMap)
        }

        @Test
        fun `Should handle updatable status for CANCELED`() {
            // Given
            val currentStatus = TransactionStatusEnum.CANCELED
            val statusUpdatableMap = hashMapOf(
                TransactionStatusEnum.PENDING_CREATION to false,
                TransactionStatusEnum.CREATED to false,
                TransactionStatusEnum.PROCESSING to false,
                TransactionStatusEnum.CANCELED to false,
                TransactionStatusEnum.FAILED to false,
                TransactionStatusEnum.REFUNDED to false,
                TransactionStatusEnum.SUCCESS to false
            )

            // When / Then
            assertUpdatableStatus(currentStatus, statusUpdatableMap)
        }

        @Test
        fun `Should handle updatable status for FAILED`() {
            // Given
            val currentStatus = TransactionStatusEnum.FAILED
            val statusUpdatableMap = hashMapOf(
                TransactionStatusEnum.PENDING_CREATION to false,
                TransactionStatusEnum.CREATED to false,
                TransactionStatusEnum.PROCESSING to false,
                TransactionStatusEnum.CANCELED to false,
                TransactionStatusEnum.FAILED to false,
                TransactionStatusEnum.REFUNDED to false,
                TransactionStatusEnum.SUCCESS to false
            )

            // When / Then
            assertUpdatableStatus(currentStatus, statusUpdatableMap)
        }

        @Test
        fun `Should handle updatable status for REFUNDED`() {
            // Given
            val currentStatus = TransactionStatusEnum.FAILED
            val statusUpdatableMap = hashMapOf(
                TransactionStatusEnum.PENDING_CREATION to false,
                TransactionStatusEnum.CREATED to false,
                TransactionStatusEnum.PROCESSING to false,
                TransactionStatusEnum.CANCELED to false,
                TransactionStatusEnum.FAILED to false,
                TransactionStatusEnum.REFUNDED to false,
                TransactionStatusEnum.SUCCESS to false
            )

            // When / Then
            assertUpdatableStatus(currentStatus, statusUpdatableMap)
        }

        @Test
        fun `Should handle updatable status for SUCCESS`() {
            // Given
            val currentStatus = TransactionStatusEnum.SUCCESS
            val statusUpdatableMap = hashMapOf(
                TransactionStatusEnum.PENDING_CREATION to false,
                TransactionStatusEnum.CREATED to false,
                TransactionStatusEnum.PROCESSING to false,
                TransactionStatusEnum.CANCELED to false,
                TransactionStatusEnum.FAILED to false,
                TransactionStatusEnum.REFUNDED to true,
                TransactionStatusEnum.SUCCESS to false
            )

            // When / Then
            assertUpdatableStatus(currentStatus, statusUpdatableMap)
        }
    }
}