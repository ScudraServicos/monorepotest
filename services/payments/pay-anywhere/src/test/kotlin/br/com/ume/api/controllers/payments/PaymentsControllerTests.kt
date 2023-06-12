package br.com.ume.api.controllers.payments

import br.com.ume.api.controllers.payments.dtos.CreatePaymentRequest
import br.com.ume.api.transformObjectFromSerializer
import br.com.ume.application.features.payments.createPayment.useCase.CreatePaymentUseCase
import br.com.ume.application.shared.payment.repository.dtos.PaymentDto
import br.com.ume.application.shared.payment.repository.dtos.PaymentOriginDto
import br.com.ume.application.shared.testBuilders.ContractBuilder.Companion.buildContract
import br.com.ume.application.shared.testBuilders.ContractBuilder.Companion.buildProposal
import br.com.ume.application.shared.utils.CustomDeserializer
import br.com.ume.application.shared.utils.utcNow
import com.google.gson.Gson
import io.micronaut.http.HttpStatus
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.restassured.specification.RequestSpecification
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.times
import java.util.*

@MicronautTest
class PaymentsControllerTests {

    companion object {
        private const val defaultToken = "user.jwt.token"
        private const val defaultXForwardedFor = "192.168.0.1,192.168.0.2"
        private const val defaultUserId = "1"
        private val defaultHeaders = mapOf("Authorization" to defaultToken, "X-Forwarded-For" to defaultXForwardedFor)
    }

    @Inject
    var createPaymentUseCaseMock: CreatePaymentUseCase = mockedCreatePaymentUseCase()

    @Test
    fun `Should create a payment`(spec: RequestSpecification) {
        val contractId = "123"
        val proposalId = "2"
        val borrowerId = "1"
        val brCode = "brCode"
        val contract = buildContract(
            contractId, borrowerId, listOf(buildProposal("2", contractId))
        )
        val utcNow = utcNow()
        val payment = PaymentDto(
            id = UUID.randomUUID(),
            externalId = 1,
            paymentOrigin = PaymentOriginDto(
                id = UUID.randomUUID(),
                externalId = 1,
                userId = contract.borrowerId,
                contractId = contract.id,
                creationTimestamp = utcNow,
                updateTimestamp = utcNow,
            ),
            value = 10.0,
            brCode = brCode,
            creationTimestamp = utcNow,
            updateTimestamp = utcNow,
        )
        Mockito.`when`(createPaymentUseCaseMock.execute(proposalId, contractId, brCode, defaultUserId, defaultHeaders))
            .thenReturn(payment)

        val response = spec
            .given()
            .header("X-API-KEY", "123")
            .header("X-Authorization", defaultToken)
            .header("X-Forwarded-For", defaultXForwardedFor)
            .body(
                Gson().toJson(
                    CreatePaymentRequest(
                        proposalId,
                        contractId,
                        brCode,
                        userId = defaultUserId,
                    )
                )
            )
            .header("Content-Type", "application/json")
            .`when`()
            .post("/api/payments")
        val responseBody = CustomDeserializer.deserialize(response.body.asString(), PaymentDto::class.java)

        Mockito.verify(createPaymentUseCaseMock, times(1))
            .execute(proposalId, contractId, brCode, defaultUserId, defaultHeaders)
        assertEquals(HttpStatus.OK.code, response.statusCode)
        assertEquals(transformObjectFromSerializer(payment), responseBody)
    }

    @Test
    fun `Should return BAD_REQUEST when request is wrong`(spec: RequestSpecification) {
        val response = spec
            .given()
            .header("X-API-KEY", "123")
            .header("X-Authorization", defaultToken)
            .header("X-Forwarded-For", defaultXForwardedFor)
            .body({})
            .header("Content-Type", "application/json")
            .`when`()
            .post("/api/payments")

        assertEquals(HttpStatus.BAD_REQUEST.code, response.statusCode)
    }

    @Test
    fun `Should return INTERNAL_SERVER_ERROR when some error occurs`(spec: RequestSpecification) {
        val contractId = "123"
        val proposalId = "2"
        val brCode = "brCode"
        Mockito.`when`(createPaymentUseCaseMock.execute(proposalId, contractId, brCode, defaultUserId, defaultHeaders))
            .thenThrow(RuntimeException("ERROR"))

        val response = spec
            .given()
            .header("X-API-KEY", "123")
            .header("X-Authorization", defaultToken)
            .header("X-FORWARDED-FOR", defaultXForwardedFor)
            .body(
                Gson().toJson(
                    CreatePaymentRequest(
                        proposalId,
                        contractId,
                        brCode,
                        userId = defaultUserId,
                    )
                )
            )
            .header("Content-Type", "application/json")
            .`when`()
            .post("/api/payments")

        Mockito.verify(createPaymentUseCaseMock, times(1))
            .execute(proposalId, contractId, brCode, defaultUserId, defaultHeaders)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.code, response.statusCode)
    }

    @MockBean(CreatePaymentUseCase::class)
    fun mockedCreatePaymentUseCase(): CreatePaymentUseCase = Mockito.mock(CreatePaymentUseCase::class.java)
}