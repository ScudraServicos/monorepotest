package br.com.ume.application.transaction.handleRefund.utils

import br.com.ume.api.controllers.pubSub.testHelpers.buildBrcodePaymentEvent
import br.com.ume.application.features.transaction.handleRefund.utils.isPaymentEventDuplicated
import br.com.ume.application.features.transaction.handleRefund.utils.isPaymentEventOutOfOrder
import br.com.ume.application.shared.transaction.testBuilders.TransactionBuilder
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import br.com.ume.application.features.transaction.handleRefund.enums.RefundTypeEnum
import br.com.ume.application.features.transaction.handleRefund.utils.getRefundType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class HandleRefundUtilsTests {
    @Nested
    @DisplayName("isPaymentEventDuplicated()")
    inner class IsPaymentEventDuplicated {
        @Test
        fun `Should return true if payment value is equal to transaction value`() {
            val transaction = TransactionBuilder.build(value = 12.34)
            val event = buildBrcodePaymentEvent(amount = 1234)

            val result = isPaymentEventDuplicated(transaction, event)

            assertTrue(result)
        }

        @Test
        fun `Should return false if payment value is less than transaction value`() {
            val transaction = TransactionBuilder.build(value = 12.34)
            val event = buildBrcodePaymentEvent(amount = 1233)

            val result = isPaymentEventDuplicated(transaction, event)

            assertFalse(result)
        }

        @Test
        fun `Should return false if payment value is greater than transaction value`() {
            val transaction = TransactionBuilder.build(value = 12.33)
            val event = buildBrcodePaymentEvent(amount = 1234)

            val result = isPaymentEventDuplicated(transaction, event)

            assertFalse(result)
        }
    }

    @Nested
    @DisplayName("isPaymentEventOutOfOrder()")
    inner class IsPaymentEventOutOfOrder {
        @Test
        fun `Should return true if transaction value is less than payment value`() {
            val transaction = TransactionBuilder.build(value = 12.33)
            val event = buildBrcodePaymentEvent(amount = 1234)

            val result = isPaymentEventOutOfOrder(transaction, event)

            assertTrue(result)
        }

        @Test
        fun `Should return false if transaction value is equal to payment value`() {
            val transaction = TransactionBuilder.build(value = 12.34)
            val event = buildBrcodePaymentEvent(amount = 1234)

            val result = isPaymentEventOutOfOrder(transaction, event)

            assertFalse(result)
        }

        @Test
        fun `Should return false if transaction value is greater than payment value`() {
            val transaction = TransactionBuilder.build(value = 12.34)
            val event = buildBrcodePaymentEvent(amount = 1233)

            val result = isPaymentEventOutOfOrder(transaction, event)

            assertFalse(result)
        }
    }

    @Nested
    @DisplayName("getRefundType()")
    inner class GetRefundType {
        private val defaultPaymentLeftoverCutoffValue = 1.5

        @Test
        fun `Should return TOTAL if event value is zero`() {
            val event = buildBrcodePaymentEvent(amount = 0)

            val result = getRefundType(event, defaultPaymentLeftoverCutoffValue)

            assertEquals(RefundTypeEnum.TOTAL, result)
        }

        @Test
        fun `Should return PARTIAL if event value is greater than leftover cutoff value`() {
            val event = buildBrcodePaymentEvent(amount = 151)

            val result = getRefundType(event, defaultPaymentLeftoverCutoffValue)

            assertEquals(RefundTypeEnum.PARTIAL, result)
        }

        @Test
        fun `Should return TOTAL_WITH_LEFTOVER if event value is less than leftover cutoff value`() {
            val event = buildBrcodePaymentEvent(amount = 149)

            val result = getRefundType(event, defaultPaymentLeftoverCutoffValue)

            assertEquals(RefundTypeEnum.TOTAL_WITH_LEFTOVER, result)
        }

        @Test
        fun `Should return TOTAL_WITH_LEFTOVER if event value is equal to leftover cutoff value`() {
            val event = buildBrcodePaymentEvent(amount = 150)

            val result = getRefundType(event, defaultPaymentLeftoverCutoffValue)

            assertEquals(RefundTypeEnum.TOTAL_WITH_LEFTOVER, result)
        }

        @Test
        fun `Should return TOTAL_WITH_LEFTOVER if event value is almost zero`() {
            val event = buildBrcodePaymentEvent(amount = 1)

            val result = getRefundType(event, defaultPaymentLeftoverCutoffValue)

            assertEquals(RefundTypeEnum.TOTAL_WITH_LEFTOVER, result)
        }
    }
}