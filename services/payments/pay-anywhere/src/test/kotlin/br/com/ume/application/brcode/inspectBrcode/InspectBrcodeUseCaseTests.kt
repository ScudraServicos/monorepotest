package br.com.ume.application.brcode.inspectBrcode

import br.com.ume.api.exceptions.types.BusinessRuleException
import br.com.ume.api.exceptions.types.InternalErrorException
import br.com.ume.application.features.brcode.inspectBrcode.useCase.InspectBrcodeUseCase
import br.com.ume.application.features.brcode.inspectBrcode.useCase.InspectBrcodeUseCaseImpl
import br.com.ume.application.features.brcode.inspectBrcode.useCase.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.inspectBrcode.gateway.inspectBrcode.InspectBrcodeGateway
import br.com.ume.application.features.brcode.inspectBrcode.gateway.inspectBrcode.dtos.InspectBrcodeGatewayOutput
import br.com.ume.application.shared.testBuilders.InspectedBrcodeBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito

class InspectBrcodeUseCaseTests {
    private lateinit var inspectBrcodeGatewayMock: InspectBrcodeGateway
    private lateinit var inspectBrcodeUseCase: InspectBrcodeUseCase

    companion object {
        private const val defaultBrCode = "some.brcode"
        private const val defaultUserId = "userId"
    }

    @BeforeEach
    fun setUp() {
        inspectBrcodeGatewayMock = Mockito.mock(InspectBrcodeGateway::class.java)
        inspectBrcodeUseCase = InspectBrcodeUseCaseImpl(inspectBrcodeGatewayMock)
    }

    @Test
    fun `Should return inspected brcode`() {
        // Given
        val inspectedBrcode = InspectedBrcodeBuilder.buildDynamic()
        Mockito.`when`(inspectBrcodeGatewayMock.inspect(defaultBrCode, defaultUserId)).thenReturn(
            InspectBrcodeGatewayOutput(inspectedBrcode)
        )

        // When
        val result = inspectBrcodeUseCase.execute(defaultBrCode, defaultUserId)

        // Then
        assertEquals(inspectedBrcode, result)
    }

    @Test
    fun `Should throw BusinessRuleException`() {
        // Given
        val gatewayReturn = InspectBrcodeGatewayOutput(error = InspectBrcodeErrorEnum.INVALID)
        Mockito.`when`(inspectBrcodeGatewayMock.inspect(defaultBrCode, defaultUserId)).thenReturn(gatewayReturn)

        // When / Then
        val result = assertThrows<BusinessRuleException> {
            inspectBrcodeUseCase.execute(defaultBrCode, defaultUserId)
        }

        // Then
        assertEquals(gatewayReturn.error.toString(), result.message)
    }

    @Test
    fun `Should throw InternalErrorException`() {
        // Given
        val gatewayReturn = InspectBrcodeGatewayOutput(error = InspectBrcodeErrorEnum.ERROR)
        Mockito.`when`(inspectBrcodeGatewayMock.inspect(defaultBrCode, defaultUserId)).thenReturn(gatewayReturn)

        // When / Then
        val result = assertThrows<InternalErrorException> {
            inspectBrcodeUseCase.execute(defaultBrCode, defaultUserId)
        }

        // Then
        assertEquals(gatewayReturn.error.toString(), result.message)
    }
}