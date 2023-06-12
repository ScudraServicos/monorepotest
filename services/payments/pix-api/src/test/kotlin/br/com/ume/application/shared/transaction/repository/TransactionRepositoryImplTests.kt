package br.com.ume.application.shared.transaction.repository

import br.com.ume.application.shared.transaction.builders.TransactionDtoBuilder
import br.com.ume.application.shared.transaction.enums.TransactionStatusEnum
import br.com.ume.application.shared.transaction.repository.transaction.TransactionJpaRepository
import br.com.ume.application.shared.transaction.repository.transaction.TransactionRefundJpaRepository
import br.com.ume.application.shared.transaction.repository.transaction.TransactionRepository
import br.com.ume.application.shared.transaction.repository.transaction.TransactionRepositoryImpl
import br.com.ume.application.shared.transaction.repository.transaction.dtos.TransactionRefundDto
import br.com.ume.application.shared.transaction.testBuilders.TransactionBuilder
import br.com.ume.application.shared.transaction.testBuilders.TransactionDtoTestBuilder
import br.com.ume.application.shared.transaction.testBuilders.buildCreateTransactionRefundDto
import br.com.ume.application.shared.transaction.testBuilders.buildTransactionRefundDto
import br.com.ume.application.utils.utcNow
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import java.util.*

class TransactionRepositoryImplTests {
    private lateinit var transactionJpaRepositoryMock: TransactionJpaRepository
    private lateinit var transactionRefundJpaRepositoryMock: TransactionRefundJpaRepository
    private lateinit var transactionRepository: TransactionRepository

    @BeforeEach
    fun setUp() {
        transactionJpaRepositoryMock = Mockito.mock(TransactionJpaRepository::class.java)
        transactionRefundJpaRepositoryMock = Mockito.mock(TransactionRefundJpaRepository::class.java)
        transactionRepository = TransactionRepositoryImpl(transactionJpaRepositoryMock, transactionRefundJpaRepositoryMock)
    }

    companion object {
        private const val transactionIdString = "4c1ef6f9-3d1a-46e5-8222-b3d674aff767"
        private val transactionUUID = UUID.fromString(transactionIdString)
    }

    @Nested
    @DisplayName("createTransaction()")
    inner class CreateTransaction {
        @Test
        fun `Should return id when successfully saving transaction`() {
            // Given
            val transaction = TransactionDtoTestBuilder.build(transactionUUID)
            Mockito.`when`(transactionJpaRepositoryMock.save(transaction))
                .thenReturn(transaction)

            // When
            val result = transactionRepository.createTransaction(transaction)

            // Then
            Mockito.verify(transactionJpaRepositoryMock, Mockito.times(1)).save(transaction)
            assertEquals(transactionIdString, result)
        }

        @Test
        fun `Should return null when failed saving transaction`() {
            // Given
            val transaction = TransactionDtoTestBuilder.build(transactionUUID)
            Mockito.`when`(transactionJpaRepositoryMock.save(transaction)).thenAnswer {
                throw Exception()
            }

            // When
            val result = transactionRepository.createTransaction(transaction)

            // Then
            Mockito.verify(transactionJpaRepositoryMock, Mockito.times(1)).save(transaction)
            assertNull(result)
        }
    }

    @Nested
    @DisplayName("createTransactionRefund()")
    inner class CreateTransactionRefund {
        @Test
        fun `Should return transaction refund on successful saving`() {
            // Given
            val transaction = TransactionBuilder.build()
            val transactionDto = TransactionDtoBuilder.buildFromTransaction(transaction)
            val transactionRefundDto = buildTransactionRefundDto(transactionDto)
            val createTransactionRefundDto = buildCreateTransactionRefundDto(transaction)
            // TODO(Replace any() after being able to mock static methods and mock utcNow())
            Mockito.`when`(transactionRefundJpaRepositoryMock.save(any()))
                .thenReturn(transactionRefundDto)

            // When
            val result = transactionRepository.createTransactionRefund(createTransactionRefundDto)

            // Then
            assertEquals(transactionRefundDto, result)
            Mockito.verify(transactionRefundJpaRepositoryMock, Mockito.times(1)).save(any())
        }

        @Test
        fun `Should return null when failed saving transaction refund`() {
            // Given
            val transaction = TransactionBuilder.build()
            val createTransactionRefundDto = buildCreateTransactionRefundDto(transaction)
            Mockito.`when`(transactionRefundJpaRepositoryMock.save(any())).thenAnswer { throw Exception() }

            // When
            val result = transactionRepository.createTransactionRefund(createTransactionRefundDto)

            // Then
            assertNull(result)
            Mockito.verify(transactionRefundJpaRepositoryMock, Mockito.times(1)).save(any())
        }
    }

    @Nested
    @DisplayName("getTransaction(transactionId)")
    inner class GetTransaction {
        @Test
        fun `Should return transaction if exists`() {
            // Given
            val transaction = TransactionDtoTestBuilder.build(transactionUUID)
            Mockito.`when`(transactionJpaRepositoryMock.findById(transactionUUID)).thenReturn(Optional.of(transaction))

            // When
            val result = transactionRepository.getTransaction(transactionIdString)

            // Then
            Mockito.verify(transactionJpaRepositoryMock, Mockito.times(1)).findById(transactionUUID)
            assertEquals(transaction, result)
        }

        @Test
        fun `Should return null if transaction does not exist`() {
            // Given
            Mockito.`when`(transactionJpaRepositoryMock.findById(transactionUUID)).thenReturn(Optional.empty())

            // When
            val result = transactionRepository.getTransaction(transactionIdString)

            // Then
            Mockito.verify(transactionJpaRepositoryMock, Mockito.times(1)).findById(transactionUUID)
            assertEquals(null, result)
        }
    }

    @Nested
    @DisplayName("getTransaction(product, productId)")
    inner class GetTransactionByProduct {
        @Test
        fun `Should return transaction if exists`() {
            // Given
            val referenceName = "PAY"
            val referenceId = "1"
            val transaction = TransactionDtoTestBuilder.build(transactionUUID)
            Mockito.`when`(
                transactionJpaRepositoryMock.findByOriginSourceProductReferenceNameAndOriginSourceProductReferenceId(
                    referenceName,
                    referenceId
                )
            )
                .thenReturn(Optional.of(transaction))

            // When
            val result = transactionRepository.getTransaction(referenceName, referenceId)

            // Then
            assertEquals(transaction, result)
            Mockito.verify(transactionJpaRepositoryMock, times(1))
                .findByOriginSourceProductReferenceNameAndOriginSourceProductReferenceId(referenceName, referenceId)
        }

        @Test
        fun `Should return null if transaction does not exist`() {
            // Given
            val referenceName = "PAY"
            val referenceId = "1"
            Mockito.`when`(
                transactionJpaRepositoryMock.findByOriginSourceProductReferenceNameAndOriginSourceProductReferenceId(
                    referenceName,
                    referenceId
                )
            )
                .thenReturn(Optional.empty())

            // When
            val result = transactionRepository.getTransaction(referenceName, referenceId)

            // Then
            assertNull(result)
            Mockito.verify(transactionJpaRepositoryMock, times(1))
                .findByOriginSourceProductReferenceNameAndOriginSourceProductReferenceId(referenceName, referenceId)
        }
    }

    @Nested
    @DisplayName("getTransactionByExternalId()")
    inner class GetTransactionByExternalId {
        @Test
        fun `Should return transaction by external id if exists`() {
            // Given
            val partnerExternalId = "123"
            val transaction = TransactionDtoTestBuilder.build(transactionUUID).copy(partnerExternalId = partnerExternalId)
            Mockito.`when`(transactionJpaRepositoryMock.findByPartnerExternalId(partnerExternalId))
                .thenReturn(Optional.of(transaction))

            // When
            val result = transactionRepository.getTransactionByExternalId(partnerExternalId)

            // Then
            Mockito.verify(transactionJpaRepositoryMock, Mockito.times(1))
                .findByPartnerExternalId(partnerExternalId)
            assertEquals(transaction, result)
        }

        @Test
        fun `Should return null if transaction is not found by external id`() {
            // Given
            val partnerExternalId = "000"
            Mockito.`when`(transactionJpaRepositoryMock.findByPartnerExternalId(partnerExternalId)).thenReturn(Optional.empty())

            // When
            val result = transactionRepository.getTransactionByExternalId(partnerExternalId)

            // Then
            Mockito.verify(transactionJpaRepositoryMock, Mockito.times(1))
                .findByPartnerExternalId(partnerExternalId)
            assertEquals(null, result)
        }
    }

    @Nested
    @DisplayName("updateTransactionStatus()")
    inner class UpdateTransactionStatus {
        @Test
        fun `Should return transaction on update success`() {
            // Given
            val transaction = TransactionBuilder.build()
            val status = TransactionStatusEnum.CREATED
            Mockito.`when`(
                transactionJpaRepositoryMock.updateStatus(eq(transaction.partnerExternalId!!), eq(status.toString()), any())
            ).thenReturn(1)

            // When
            val result = transactionRepository.updateTransactionStatus(transaction, status)

            // Then
            assertNotNull(result!!)
            val expectedTransaction = transaction.copy(status = status, updateTimestamp = result.updateTimestamp)
            assertEquals(expectedTransaction, result)
            Mockito.verify(transactionJpaRepositoryMock, Mockito.times(1))
                .updateStatus(eq(transaction.partnerExternalId!!), eq(status.toString()), any())
        }

        @Test
        fun `Should return null on update fail`() {
            // Given
            val transaction = TransactionBuilder.build()
            val status = TransactionStatusEnum.CREATED
            Mockito.`when`(
                transactionJpaRepositoryMock.updateStatus(eq(transaction.partnerExternalId!!), eq(status.toString()), any())
            ).thenReturn(0)

            // When
            val result = transactionRepository.updateTransactionStatus(transaction, status)

            // Then
            Mockito.verify(transactionJpaRepositoryMock, Mockito.times(1))
                .updateStatus(eq(transaction.partnerExternalId!!), eq(status.toString()), any())
            assertNull(result)
        }

        @Test
        fun `Should return null in case of an unexpected exception`() {
            // Given
            val transaction = TransactionBuilder.build()
            val status = TransactionStatusEnum.CREATED
            Mockito.`when`(
                transactionJpaRepositoryMock.updateStatus(eq(transaction.partnerExternalId!!), eq(status.toString()), any())
            ).thenAnswer {
                throw Exception()
            }

            // When
            val result = transactionRepository.updateTransactionStatus(transaction, status)

            // Then
            Mockito.verify(transactionJpaRepositoryMock, Mockito.times(1))
                .updateStatus(eq(transaction.partnerExternalId!!), eq(status.toString()), any())
            assertNull(result)
        }
    }

    @Nested
    @DisplayName("updateTransaction(id, status, partnerExternalId)")
    inner class UpdateTransactionStatusAndExternalId {
        @Test
        fun `Should return true on update success`() {
            // Given
            val status = TransactionStatusEnum.CREATED
            val partnerExternalId = "partnerExternalId"
            Mockito.`when`(
                transactionJpaRepositoryMock.update(eq(transactionUUID), eq(status.toString()), eq(partnerExternalId), any())
            ).thenReturn(1)

            // When
            val result = transactionRepository.updateTransaction(transactionIdString, status, partnerExternalId)

            // Then
            Mockito.verify(transactionJpaRepositoryMock, Mockito.times(1))
                .update(eq(transactionUUID), eq(status.toString()), eq(partnerExternalId), any())
            assertTrue(result)
        }

        @Test
        fun `Should return false on update fail`() {
            // Given
            val status = TransactionStatusEnum.CREATED
            val partnerExternalId = "partnerExternalId"
            Mockito.`when`(
                transactionJpaRepositoryMock.update(eq(transactionUUID), eq(status.toString()), eq(partnerExternalId), any())
            ).thenReturn(0)

            // When
            val result = transactionRepository.updateTransaction(transactionIdString, status, partnerExternalId)

            // Then
            Mockito.verify(transactionJpaRepositoryMock, Mockito.times(1))
                .update(eq(transactionUUID), eq(status.toString()), eq(partnerExternalId), any())
            assertFalse(result)
        }

        @Test
        fun `Should return false in case of a unexpected exception`() {
            // Given
            val status = TransactionStatusEnum.CREATED
            val partnerExternalId = "partnerExternalId"
            Mockito.`when`(
                transactionJpaRepositoryMock.update(eq(transactionUUID), eq(status.toString()), eq(partnerExternalId), any())
            ).thenAnswer {
                throw Exception()
            }

            // When
            val result = transactionRepository.updateTransaction(transactionIdString, status, partnerExternalId)

            // Then
            Mockito.verify(transactionJpaRepositoryMock, Mockito.times(1))
                .update(eq(transactionUUID), eq(status.toString()), eq(partnerExternalId), any())
            assertFalse(result)
        }
    }
}