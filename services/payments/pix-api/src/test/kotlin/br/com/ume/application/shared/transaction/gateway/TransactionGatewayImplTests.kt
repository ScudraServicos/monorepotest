package br.com.ume.application.shared.transaction.gateway

import br.com.ume.application.features.brcode.shared.brcodeInspected.services.paymentService.PaymentService
import br.com.ume.application.features.transaction.handleRefund.enums.RefundTypeEnum
import br.com.ume.application.shared.transaction.builders.TransactionBuilder
import br.com.ume.application.shared.transaction.builders.TransactionDtoBuilder
import br.com.ume.application.shared.transaction.domain.Transaction
import br.com.ume.application.shared.transaction.enums.TransactionStatusEnum
import br.com.ume.application.shared.transaction.repository.transaction.TransactionRepository
import br.com.ume.application.shared.transaction.repository.transaction.filter.TransactionFilter
import br.com.ume.application.shared.transaction.testBuilders.*
import br.com.ume.application.shared.transaction.testBuilders.TransactionBuilder as TransactionTestBuilder
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import java.util.*

class TransactionGatewayImplTests {
    private lateinit var transactionRepository: TransactionRepository
    private lateinit var paymentServiceMock: PaymentService
    private lateinit var transactionGateway: TransactionGateway

    @BeforeEach
    fun setUp() {
        transactionRepository = Mockito.mock(TransactionRepository::class.java)
        paymentServiceMock = Mockito.mock(PaymentService::class.java)
        transactionGateway = TransactionGatewayImpl(transactionRepository, paymentServiceMock)
    }

    companion object {
        private const val transactionIdString = "4c1ef6f9-3d1a-46e5-8222-b3d674aff767"
        private val transactionUUID = UUID.fromString(transactionIdString)
    }

    @Nested
    @DisplayName("createBrcodeTransaction()")
    inner class CreateBrcodeTransaction {
        @Test
        fun `Should return id when successfully saving transaction`() {
            // Given
            val input = CreateBrcodeTransactionDtoTestBuilder.build()
            Mockito.`when`(transactionRepository.createTransaction(any()))
                .thenReturn(transactionIdString)

            // When
            val result = transactionGateway.createBrcodeTransaction(input)

            // Then
            Mockito.verify(transactionRepository, Mockito.times(1)).createTransaction(any())
            assertEquals(transactionIdString, result)
        }

        @Test
        fun `Should return null when failed saving transaction`() {
            // Given
            val input = CreateBrcodeTransactionDtoTestBuilder.build()
            Mockito.`when`(transactionRepository.createTransaction(any())).thenReturn(null)

            // When
            val result = transactionGateway.createBrcodeTransaction(input)

            // Then
            Mockito.verify(transactionRepository, Mockito.times(1)).createTransaction(any())
            assertNull(result)
        }
    }

    @Nested
    @DisplayName("createTransactionRefund()")
    inner class CreateTransactionRefund {
        @Test
        fun `Should return transaction refund on successful saving`() {
            // Given
            val transaction = TransactionTestBuilder.build()
            val transactionDto = TransactionDtoBuilder.buildFromTransaction(transaction)
            val transactionRefundDto = buildTransactionRefundDto(transactionDto)
            val transactionRefund = buildTransactionRefund(
                id = transactionRefundDto.id.toString(),
                timestamp = transactionRefundDto.creationTimestamp,
                typeEnum = RefundTypeEnum.valueOf(transactionRefundDto.type)
            )
            val createTransactionRefundDto = buildCreateTransactionRefundDto(transaction)
            Mockito.`when`(transactionRepository.createTransactionRefund(createTransactionRefundDto))
                .thenReturn(transactionRefundDto)

            // When
            val result = transactionGateway.createTransactionRefund(createTransactionRefundDto)

            // Then
            assertEquals(transactionRefund, result)
            Mockito.verify(transactionRepository, Mockito.times(1))
                .createTransactionRefund(createTransactionRefundDto)
        }

        @Test
        fun `Should return null when failed saving transaction refund`() {
            // Given
            val transaction = TransactionTestBuilder.build()
            val createTransactionRefundDto = buildCreateTransactionRefundDto(transaction)
            Mockito.`when`(transactionRepository.createTransactionRefund(createTransactionRefundDto))
                .thenReturn(null)

            // When
            val result = transactionGateway.createTransactionRefund(createTransactionRefundDto)

            // Then
            assertNull(result)
            Mockito.verify(transactionRepository, Mockito.times(1))
                .createTransactionRefund(createTransactionRefundDto)
        }
    }

    @Nested
    @DisplayName("getTransaction(transactionId)")
    inner class GetTransaction {
        @Test
        fun `Should return transaction if exists`() {
            // Given
            val transaction = TransactionDtoTestBuilder.build(transactionUUID)
            Mockito.`when`(transactionRepository.getTransaction(transactionIdString)).thenReturn(transaction)

            // When
            val result = transactionGateway.getTransaction(transactionIdString)

            // Then
            val expectedResult = TransactionBuilder.buildFromEntity(transaction)
            assertEquals(expectedResult, result)
        }

        @Test
        fun `Should return null if transaction does not exist`() {
            // Given
            Mockito.`when`(transactionRepository.getTransaction(transactionIdString)).thenReturn(null)

            // When
            val result = transactionGateway.getTransaction(transactionIdString)

            // Then
            assertEquals(null, result)
        }
    }

    @Nested
    @DisplayName("getTransaction(product, productId)")
    inner class GetTransactionByProduct {
        @Test
        fun `Should return transaction if exists`() {
            // Given
            val product = "PAY"
            val productId = "1"
            val transaction = TransactionDtoTestBuilder.build(transactionUUID)
            Mockito.`when`(transactionRepository.getTransaction(product, productId)).thenReturn(transaction)

            // When
            val result = transactionGateway.getTransaction(product, productId)

            // Then
            val expectedResult = TransactionBuilder.buildFromEntity(transaction)
            assertEquals(expectedResult, result)
            Mockito.verify(transactionRepository, times(1)).getTransaction(product, productId)
        }

        @Test
        fun `Should return null if transaction does not exist`() {
            // Given
            val product = "PAY"
            val productId = "1"
            Mockito.`when`(transactionRepository.getTransaction(product, productId)).thenReturn(null)

            // When
            val result = transactionGateway.getTransaction(product, productId)

            // Then
            assertNull(result)
            Mockito.verify(transactionRepository, times(1)).getTransaction(product, productId)
        }
    }

    @Nested
    @DisplayName("updateTransactionStatus()")
    inner class UpdateTransactionStatus {
        @Test
        fun `Should return true if transaction is updated`() {
            // Given
            val transaction = TransactionTestBuilder.build()
            val status = TransactionStatusEnum.CREATED
            val updatedTransaction = transaction.copy(status = status)
            Mockito.`when`(transactionRepository.updateTransactionStatus(transaction, status)).thenReturn(updatedTransaction)

            // When
            val result = transactionGateway.updateTransactionStatus(transaction, status)

            // Then
            assertNotNull(result!!)
            assertEquals(updatedTransaction.copy(updateTimestamp = result.updateTimestamp), result)
        }

        @Test
        fun `Should return false if transaction is not updated`() {
            // Given
            val transaction = TransactionTestBuilder.build()
            val status = TransactionStatusEnum.CREATED
            Mockito.`when`(transactionRepository.updateTransactionStatus(transaction, status)).thenReturn(null)

            // When
            val result = transactionGateway.updateTransactionStatus(transaction, status)

            // Then
            assertNull(result)
        }
    }

    @Nested
    @DisplayName("updateTransactionForRefund()")
    inner class UpdateTransactionForRefund {
        private val defaultValue = 15.0
        private val defaultTransaction = TransactionTestBuilder.build()

        @Test
        fun `Should return updated transaction`() {
            // Given
            val updatedTransaction = defaultTransaction.copy(value = defaultValue)
            Mockito.`when`(transactionRepository.updateTransactionForRefund(defaultTransaction, defaultValue))
                .thenReturn(updatedTransaction)

            // When
            val result = transactionGateway.updateTransactionForRefund(defaultTransaction, defaultValue)

            // Then
            assertNotNull(result!!)
            assertEquals(updatedTransaction.copy(updateTimestamp = result.updateTimestamp), result)
        }

        @Test
        fun `Should return null if transaction update fails`() {
            // Given
            Mockito.`when`(transactionRepository.updateTransactionForRefund(defaultTransaction, defaultValue))
                .thenReturn(null)

            // When
            val result = transactionGateway.updateTransactionForRefund(defaultTransaction, defaultValue)

            // Then
            assertNull(result)
        }
    }

    @Nested
    @DisplayName("updateTransactionAsRefunded()")
    inner class UpdateTransactionAsRefunded {
        private val defaultValue = 15.0
        private val defaultStatus = TransactionStatusEnum.REFUNDED
        private val defaultTransaction = TransactionTestBuilder.build()

        @Test
        fun `Should return updated transaction`() {
            // Given
            val updatedTransaction = defaultTransaction.copy(value = defaultValue, status = defaultStatus)
            Mockito.`when`(transactionRepository.updateTransactionAsRefunded(defaultTransaction, defaultValue, defaultStatus))
                .thenReturn(updatedTransaction)

            // When
            val result = transactionGateway.updateTransactionAsRefunded(defaultTransaction, defaultValue, defaultStatus)

            // Then
            assertNotNull(result!!)
            assertEquals(updatedTransaction.copy(updateTimestamp = result.updateTimestamp), result)
        }

        @Test
        fun `Should return null if transaction update fails`() {
            // Given
            Mockito.`when`(transactionRepository.updateTransactionAsRefunded(defaultTransaction, defaultValue, defaultStatus))
                .thenReturn(null)

            // When
            val result = transactionGateway.updateTransactionAsRefunded(defaultTransaction, defaultValue, defaultStatus)

            // Then
            assertNull(result)
        }
    }

    @Nested
    @DisplayName("getBankingPartnerPaymentByProduct()")
    inner class GetBankingPartnerPaymentByProduct {
        @Test
        fun `Should return transaction`() {
            // Given
            val productId = "123"
            val sourceProduct = "PAY"
            val bankingPartnerPayment = BankingPartnerPaymentDtoTestBuilder.build()
            Mockito.`when`(paymentServiceMock.getBrcodePaymentByProduct(productId, sourceProduct)).thenReturn(bankingPartnerPayment)

            // When
            val result = transactionGateway.getBankingPartnerPaymentByProduct(productId, sourceProduct)

            // Then
            assertEquals(bankingPartnerPayment, result)
            Mockito.verify(paymentServiceMock, times(1)).getBrcodePaymentByProduct(productId, sourceProduct)
        }

        @Test
        fun `Should return null if transaction is not found`() {
            // Given
            val productId = "123"
            val sourceProduct = "PAY"
            Mockito.`when`(paymentServiceMock.getBrcodePaymentByProduct(productId, sourceProduct)).thenReturn(null)

            // When
            val result = transactionGateway.getBankingPartnerPaymentByProduct(productId, sourceProduct)

            // Then
            assertNull(result)
            Mockito.verify(paymentServiceMock, times(1)).getBrcodePaymentByProduct(productId, sourceProduct)
        }

        @Test
        fun `Should throw if partner throws error`() {
            // Given
            val productId = "123"
            val sourceProduct = "PAY"
            Mockito.`when`(paymentServiceMock.getBrcodePaymentByProduct(productId, sourceProduct)).thenAnswer {
                throw Exception()
            }

            // When / Then
            assertThrows<Exception> {
                transactionGateway.getBankingPartnerPaymentByProduct(productId, sourceProduct)
            }
            Mockito.verify(paymentServiceMock, times(1)).getBrcodePaymentByProduct(productId, sourceProduct)
        }
    }

    @Nested
    @DisplayName("updateTransaction(transactionId, status, partnerExternalId)")
    inner class UpdateTransaction {
        @Test
        fun `Should return true if transaction is updated`() {
            // Given
            val status = TransactionStatusEnum.CREATED
            val partnerExternalId = "partnerExternalId"
            Mockito.`when`(transactionRepository.updateTransaction(transactionIdString, status, partnerExternalId)).thenReturn(true)

            // When
            val result = transactionGateway.updateTransaction(transactionIdString, status, partnerExternalId)

            // Then
            assertTrue(result)
            Mockito.verify(transactionRepository, times(1)).updateTransaction(transactionIdString, status, partnerExternalId)
        }

        @Test
        fun `Should return false if transaction is not updated`() {
            // Given
            val status = TransactionStatusEnum.CREATED
            val partnerExternalId = "partnerExternalId"
            Mockito.`when`(transactionRepository.updateTransaction(transactionIdString, status, partnerExternalId)).thenReturn(false)

            // When
            val result = transactionGateway.updateTransaction(transactionIdString, status, partnerExternalId)

            // Then
            assertFalse(result)
            Mockito.verify(transactionRepository, times(1)).updateTransaction(transactionIdString, status, partnerExternalId)
        }
    }

    @Nested
    @DisplayName("GetTransactions(filter)")
    inner class GetTransactions {
        @Test
        fun `Should return transactions`() {
            // Given
            val filter = TransactionFilter(transactionId = transactionIdString)
            val transactionDto = TransactionDtoTestBuilder.build(transactionUUID)
            val transactionDtoList = listOf(transactionDto)
            Mockito.`when`(transactionRepository.getTransactions(filter)).thenReturn(transactionDtoList)

            // When
            val result = transactionGateway.getTransactions(filter)

            // Then
            val expectedResult = listOf(TransactionBuilder.buildFromEntity(transactionDto))
            assertEquals(expectedResult, result)
            Mockito.verify(transactionRepository, times(1)).getTransactions(filter)
        }

        @Test
        fun `Should return empty list case no transaction found`() {
            // Given
            val filter = TransactionFilter(transactionId = "4c1ef6f9-3d1a-46e5-8222-b3d674aff767")
            Mockito.`when`(transactionRepository.getTransactions(filter)).thenReturn(emptyList())

            // When
            val result = transactionGateway.getTransactions(filter)

            // Then
            assertEquals(emptyList<Transaction>(), result)
            Mockito.verify(transactionRepository, times(1)).getTransactions(filter)
        }
    }
}