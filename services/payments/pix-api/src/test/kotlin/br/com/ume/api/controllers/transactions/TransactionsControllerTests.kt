package br.com.ume.api.controllers.transactions

import br.com.ume.api.exceptions.base.ApiError
import br.com.ume.api.exceptions.types.NotFoundException
import br.com.ume.api.transformObjectFromSerializer
import br.com.ume.application.features.transaction.getTransaction.enums.GetTransactionErrorEnum
import br.com.ume.application.features.transaction.getTransaction.useCase.GetTransactionUseCaseImpl
import br.com.ume.application.features.transaction.getTransaction.useCase.GetTransactionUseCaseInput
import br.com.ume.application.shared.transaction.domain.Transaction
import br.com.ume.application.shared.transaction.testBuilders.TransactionBuilder
import br.com.ume.application.utils.CustomDeserializer
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.restassured.specification.RequestSpecification
import jakarta.inject.Inject
import org.junit.jupiter.api.*
import org.mockito.Mockito
import org.mockito.Mockito.times
import java.util.*

@MicronautTest
class TransactionsControllerTests {
    @Inject
    lateinit var getTransactionUseCase: GetTransactionUseCaseImpl

    companion object {
        private val transactionId = "4c1ef6f9-3d1a-46e5-8222-b3d674aff767"
    }

    @Nested
    @DisplayName("GET /?{transactionsParams}")
    inner class GetTransaction {
        @Test
        fun `Should return transaction when transaction found`(spec: RequestSpecification) {
            // Given
            val transaction = TransactionBuilder.build()
            val useCaseInput = GetTransactionUseCaseInput(
                transactionId = transactionId,
                sourceProductName = null,
                sourceProductId = null
            )
            Mockito.`when`(getTransactionUseCase.execute(useCaseInput)).thenReturn(transaction)

            // When
            val response = spec
                .given()
                .header("X-API-KEY", "123")
                .`when`()
                .get("/api/transactions?transactionId=$transactionId")

            val body = CustomDeserializer.deserialize(response.body().asString(), Transaction::class.java)

            // Then
            Mockito.verify(getTransactionUseCase, times(1)).execute(useCaseInput)
            Assertions.assertEquals(200, response.statusCode)
            Assertions.assertEquals(transformObjectFromSerializer(transaction), body)
        }

        @Test
        fun `Should return 404 when transaction is not found`(spec: RequestSpecification) {
            // Given
            val useCaseInput = GetTransactionUseCaseInput(
                transactionId = transactionId,
                sourceProductName = null,
                sourceProductId = null
            )
            Mockito.`when`(getTransactionUseCase.execute(useCaseInput)).thenAnswer {
                throw NotFoundException(GetTransactionErrorEnum.TRANSACTION_NOT_FOUND.toString())
            }

            // When
            val response = spec
                .given()
                .header("X-API-KEY", "123")
                .`when`()
                .get("/api/transactions?transactionId=$transactionId")

            val body = CustomDeserializer.deserialize(response.body().asString(), ApiError::class.java)

            // Then
            Mockito.verify(getTransactionUseCase, times(1)).execute(useCaseInput)
            Assertions.assertEquals(404, response.statusCode)
            Assertions.assertEquals(GetTransactionErrorEnum.TRANSACTION_NOT_FOUND.toString(), body.message)
        }
    }

    @MockBean(GetTransactionUseCaseImpl::class)
    fun mockedGetTransactionUseCase() = Mockito.mock(GetTransactionUseCaseImpl::class.java)
}