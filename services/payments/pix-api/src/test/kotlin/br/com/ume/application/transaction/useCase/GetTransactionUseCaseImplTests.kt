package br.com.ume.application.transaction.useCase

import br.com.ume.api.exceptions.types.NotFoundException
import br.com.ume.application.features.transaction.getTransaction.enums.GetTransactionErrorEnum
import br.com.ume.application.features.transaction.getTransaction.useCase.GetTransactionUseCaseImpl
import br.com.ume.application.features.transaction.getTransaction.useCase.GetTransactionUseCaseInput
import br.com.ume.application.features.transaction.getTransaction.useCase.GetTransactionUseCase
import br.com.ume.application.shared.transaction.gateway.TransactionGateway
import br.com.ume.application.shared.transaction.repository.transaction.filter.TransactionFilter
import br.com.ume.application.shared.transaction.testBuilders.TransactionBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito

class GetTransactionUseCaseImplTests {
    private lateinit var transactionGateway: TransactionGateway
    private lateinit var getTransactionUseCase: GetTransactionUseCase

    @BeforeEach
    fun setUp() {
        transactionGateway = Mockito.mock(TransactionGateway::class.java)
        getTransactionUseCase = GetTransactionUseCaseImpl(transactionGateway)
    }

    companion object {
        private const val transactionId = "4c1ef6f9-3d1a-46e5-8222-b3d674aff767"
    }

    @Test
    fun `Should return transaction if exists`() {
        // Given
        val transaction = TransactionBuilder.build(transactionId)
        val useCaseInput = GetTransactionUseCaseInput(
            transactionId = transactionId,
            sourceProductName = null,
            sourceProductId = null
        )
        val transactionFilter = TransactionFilter(
            transactionId = transactionId,
            sourceProductName = null,
            sourceProductId = null
        )
        Mockito.`when`(transactionGateway.getTransactions(transactionFilter)).thenReturn(listOf(transaction))

        // When / Then
        val result = assertDoesNotThrow { getTransactionUseCase.execute(useCaseInput) }
        assertEquals(transaction, result)
    }

    @Test
    fun `Should throw NotFound if transaction is not found`() {
        // Given
        val useCaseInput = GetTransactionUseCaseInput(
            transactionId = transactionId,
            sourceProductName = null,
            sourceProductId = null
        )
        val filter = TransactionFilter(transactionId = useCaseInput.transactionId)
        Mockito.`when`(transactionGateway.getTransactions(filter)).thenReturn(emptyList())

        // When / Then
        val exception = assertThrows<NotFoundException> { getTransactionUseCase.execute(useCaseInput) }
        assertEquals(GetTransactionErrorEnum.TRANSACTION_NOT_FOUND.toString(), exception.message)
    }
}