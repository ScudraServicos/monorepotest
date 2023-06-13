package br.com.ume.application.brcode.payBrcode.useCase

import br.com.ume.api.exceptions.types.InternalErrorException
import br.com.ume.application.brcode.shared.testBuilders.BrcodeInspectedBuilder
import br.com.ume.application.features.brcode.payBrcode.errors.PayBrcodeErrorEnum
import br.com.ume.application.features.brcode.payBrcode.gateways.PayBrcodeGateway
import br.com.ume.application.features.brcode.payBrcode.useCase.PayBrcodeUseCase
import br.com.ume.application.features.brcode.payBrcode.useCase.PayBrcodeUseCaseImpl
import br.com.ume.application.features.brcode.payBrcode.useCase.dtos.TransactionOriginDto
import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.gateways.brcodeInspection.BrcodeInspectionGateway
import br.com.ume.application.features.brcode.shared.brcodeInspected.gateways.brcodeInspection.dtos.InspectBrcodeOutput
import br.com.ume.application.shared.transaction.enums.TransactionStatusEnum
import br.com.ume.application.shared.transaction.gateway.TransactionGateway
import br.com.ume.application.shared.transaction.testBuilders.BankingPartnerPaymentDtoTestBuilder
import br.com.ume.application.shared.transaction.testBuilders.TransactionBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.never


class PayBrcodeUseCaseTests {
    private lateinit var payBrcodeGatewayMock: PayBrcodeGateway
    private lateinit var brcodeInspectionGatewayMock: BrcodeInspectionGateway
    private lateinit var transactionGatewayMock: TransactionGateway
    private lateinit var payBrcodeUseCase: PayBrcodeUseCase

    private val defaultBrcode = "123.321"
    private val defaultTransactionOrigin = TransactionOriginDto(
        userId = null,
        sourceProductReferenceId = "123",
        sourceProductReferenceName = "PAY"
    )
    private val defaultBrcodeInspected = BrcodeInspectedBuilder.buildStatic()
    private val defaultTransactionId = "4c1ef6f9-3d1a-46e5-8222-b3d674aff767"
    private val defaultTransaction = TransactionBuilder.build(defaultTransactionId)
    private val defaultBankingPartnerPaymentDto = BankingPartnerPaymentDtoTestBuilder.build()
    private val defaultBankingPartnerPaymentStatus = TransactionStatusEnum.fromBankingPartnerStatus(defaultBankingPartnerPaymentDto.status)!!

    @BeforeEach
    fun setUp() {
        payBrcodeGatewayMock = mock(PayBrcodeGateway::class.java)
        brcodeInspectionGatewayMock = mock(BrcodeInspectionGateway::class.java)
        transactionGatewayMock = mock(TransactionGateway::class.java)
        payBrcodeUseCase = PayBrcodeUseCaseImpl(payBrcodeGatewayMock, brcodeInspectionGatewayMock, transactionGatewayMock)
    }

    @Nested
    @DisplayName("When transaction does not exist")
    inner class WhenTransactionDoesNotExist {
        @Test
        fun `Should create transaction and pay brcode`() {
            // Given
            `when`(brcodeInspectionGatewayMock.inspectBrcode(defaultBrcode)).thenReturn(InspectBrcodeOutput(defaultBrcodeInspected))
            `when`(transactionGatewayMock.getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )).thenReturn(null)
            `when`(transactionGatewayMock.createBrcodeTransaction(any())).thenReturn(defaultTransactionId)
            `when`(transactionGatewayMock.getTransaction(defaultTransactionId)).thenReturn(defaultTransaction)
            `when`(transactionGatewayMock.getBankingPartnerPaymentByProduct(
                defaultTransactionOrigin.sourceProductReferenceId, defaultTransactionOrigin.sourceProductReferenceName
            )).thenReturn(null)
            `when`(payBrcodeGatewayMock.payBrcode(defaultBrcode, defaultBrcodeInspected.pixBeneficiary, defaultTransactionOrigin)).thenReturn(defaultBankingPartnerPaymentDto.id)
            `when`(transactionGatewayMock.updateTransaction(defaultTransactionId, TransactionStatusEnum.CREATED, defaultBankingPartnerPaymentDto.id)).thenReturn(true)

            // When
            val result = payBrcodeUseCase.execute(defaultBrcode, defaultTransactionOrigin)

            // Then
            assertEquals(defaultTransaction, result)
            verify(brcodeInspectionGatewayMock, times(1)).inspectBrcode(defaultBrcode)
            verify(transactionGatewayMock, times(1)).getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )
            verify(transactionGatewayMock, times(1)).createBrcodeTransaction(any())
            verify(transactionGatewayMock, times(1)).getTransaction(defaultTransactionId)
            verify(transactionGatewayMock, times(1)).getBankingPartnerPaymentByProduct(
                defaultTransactionOrigin.sourceProductReferenceId, defaultTransactionOrigin.sourceProductReferenceName
            )
            verify(payBrcodeGatewayMock, times(1)).payBrcode(defaultBrcode, defaultBrcodeInspected.pixBeneficiary, defaultTransactionOrigin)
            verify(transactionGatewayMock, times(1)).updateTransaction(defaultTransactionId, TransactionStatusEnum.CREATED, defaultBankingPartnerPaymentDto.id)
        }

        @Test
        fun `Should throw if brcode inspection returns error`() {
            // Given
            `when`(brcodeInspectionGatewayMock.inspectBrcode(defaultBrcode))
                .thenReturn(InspectBrcodeOutput(error = InspectBrcodeErrorEnum.BANKING_PARTNER_ERROR))

            // When
            val exception = assertThrows<InternalErrorException> {
                payBrcodeUseCase.execute(defaultBrcode, defaultTransactionOrigin)
            }

            // Then
            assertEquals(InspectBrcodeErrorEnum.BANKING_PARTNER_ERROR.toString(), exception.message)
            verify(brcodeInspectionGatewayMock, times(1)).inspectBrcode(defaultBrcode)
            verify(transactionGatewayMock, never()).getTransaction(anyString(), anyString())
            verify(transactionGatewayMock, never()).createBrcodeTransaction(any())
            verify(transactionGatewayMock, never()).getTransaction(defaultTransactionId)
            verify(transactionGatewayMock, never()).getBankingPartnerPaymentByProduct(anyString(), anyString())
            verify(payBrcodeGatewayMock, never()).payBrcode(anyString(), any(), any())
            verify(transactionGatewayMock, never()).updateTransaction(anyString(), any(), anyString())
        }

        @Test
        fun `Should throw if brcode inspection fails`() {
            // Given
            `when`(brcodeInspectionGatewayMock.inspectBrcode(defaultBrcode))
                .thenAnswer { throw Exception("InspectionFailed") }

            // When
            val exception = assertThrows<Exception> {
                payBrcodeUseCase.execute(defaultBrcode, defaultTransactionOrigin)
            }

            // Then
            assertEquals("InspectionFailed", exception.message)
            verify(brcodeInspectionGatewayMock, times(1)).inspectBrcode(defaultBrcode)
            verify(transactionGatewayMock, never()).getTransaction(anyString(), anyString())
            verify(transactionGatewayMock, never()).createBrcodeTransaction(any())
            verify(transactionGatewayMock, never()).getTransaction(defaultTransactionId)
            verify(transactionGatewayMock, never()).getBankingPartnerPaymentByProduct(anyString(), anyString())
            verify(payBrcodeGatewayMock, never()).payBrcode(anyString(), any(), any())
            verify(transactionGatewayMock, never()).updateTransaction(anyString(), any(), anyString())
        }

        @Test
        fun `Should throw if get transaction fails`() {
            // Given
            `when`(brcodeInspectionGatewayMock.inspectBrcode(defaultBrcode)).thenReturn(InspectBrcodeOutput(defaultBrcodeInspected))
            `when`(transactionGatewayMock.getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )).thenAnswer { throw Exception("GetTransactionFailed") }

            // When
            val exception = assertThrows<Exception> {
                payBrcodeUseCase.execute(defaultBrcode, defaultTransactionOrigin)
            }

            // Then
            assertEquals("GetTransactionFailed", exception.message)
            verify(brcodeInspectionGatewayMock, times(1)).inspectBrcode(defaultBrcode)
            verify(transactionGatewayMock, times(1)).getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )
            verify(transactionGatewayMock, never()).createBrcodeTransaction(any())
            verify(transactionGatewayMock, never()).getTransaction(defaultTransactionId)
            verify(transactionGatewayMock, never()).getBankingPartnerPaymentByProduct(anyString(), anyString())
            verify(payBrcodeGatewayMock, never()).payBrcode(anyString(), any(), any())
            verify(transactionGatewayMock, never()).updateTransaction(anyString(), any(), anyString())
        }

        @Test
        fun `Should throw if transaction is not created`() {
            // Given
            `when`(brcodeInspectionGatewayMock.inspectBrcode(defaultBrcode)).thenReturn(InspectBrcodeOutput(defaultBrcodeInspected))
            `when`(transactionGatewayMock.getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )).thenReturn(null)
            `when`(transactionGatewayMock.createBrcodeTransaction(any())).thenReturn(null)

            // When
            val exception = assertThrows<InternalErrorException> {
                payBrcodeUseCase.execute(defaultBrcode, defaultTransactionOrigin)
            }

            // Then
            assertEquals(PayBrcodeErrorEnum.TRANSACTION_CREATION_ERROR.toString(), exception.message)
            verify(brcodeInspectionGatewayMock, times(1)).inspectBrcode(defaultBrcode)
            verify(transactionGatewayMock, times(1)).getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )
            verify(transactionGatewayMock, times(1)).createBrcodeTransaction(any())
            verify(transactionGatewayMock, never()).getTransaction(defaultTransactionId)
            verify(transactionGatewayMock, never()).getBankingPartnerPaymentByProduct(anyString(), anyString())
            verify(payBrcodeGatewayMock, never()).payBrcode(anyString(), any(), any())
            verify(transactionGatewayMock, never()).updateTransaction(anyString(), any(), anyString())
        }

        @Test
        fun `Should throw if transaction creation fails`() {
            // Given
            `when`(brcodeInspectionGatewayMock.inspectBrcode(defaultBrcode)).thenReturn(InspectBrcodeOutput(defaultBrcodeInspected))
            `when`(transactionGatewayMock.getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )).thenReturn(null)
            `when`(transactionGatewayMock.createBrcodeTransaction(any()))
                .thenAnswer { throw Exception("TransactionCreationFailed") }

            // When
            val exception = assertThrows<Exception> {
                payBrcodeUseCase.execute(defaultBrcode, defaultTransactionOrigin)
            }

            // Then
            assertEquals("TransactionCreationFailed", exception.message)
            verify(brcodeInspectionGatewayMock, times(1)).inspectBrcode(defaultBrcode)
            verify(transactionGatewayMock, times(1)).getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )
            verify(transactionGatewayMock, times(1)).createBrcodeTransaction(any())
            verify(transactionGatewayMock, never()).getTransaction(defaultTransactionId)
            verify(transactionGatewayMock, never()).getBankingPartnerPaymentByProduct(anyString(), anyString())
            verify(payBrcodeGatewayMock, never()).payBrcode(anyString(), any(), any())
            verify(transactionGatewayMock, never()).updateTransaction(anyString(), any(), anyString())
        }

        @Test
        fun `Should throw if transaction is not found after creation`() {
            // Given
            `when`(brcodeInspectionGatewayMock.inspectBrcode(defaultBrcode)).thenReturn(InspectBrcodeOutput(defaultBrcodeInspected))
            `when`(transactionGatewayMock.getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )).thenReturn(null)
            `when`(transactionGatewayMock.createBrcodeTransaction(any())).thenReturn(defaultTransactionId)
            `when`(transactionGatewayMock.getTransaction(defaultTransactionId)).thenReturn(null)

            // When
            val exception = assertThrows<InternalErrorException> {
                payBrcodeUseCase.execute(defaultBrcode, defaultTransactionOrigin)
            }

            // Then
            assertEquals(PayBrcodeErrorEnum.TRANSACTION_NOT_FOUND.toString(), exception.message)
            verify(brcodeInspectionGatewayMock, times(1)).inspectBrcode(defaultBrcode)
            verify(transactionGatewayMock, times(1)).getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )
            verify(transactionGatewayMock, times(1)).createBrcodeTransaction(any())
            verify(transactionGatewayMock, times(1)).getTransaction(defaultTransactionId)
            verify(transactionGatewayMock, never()).getBankingPartnerPaymentByProduct(anyString(), anyString())
            verify(payBrcodeGatewayMock, never()).payBrcode(anyString(), any(), any())
            verify(transactionGatewayMock, never()).updateTransaction(anyString(), any(), anyString())
        }

        @Test
        fun `Should throw if banking partner payment is not created`() {
            // Given
            `when`(brcodeInspectionGatewayMock.inspectBrcode(defaultBrcode)).thenReturn(InspectBrcodeOutput(defaultBrcodeInspected))
            `when`(transactionGatewayMock.getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )).thenReturn(null)
            `when`(transactionGatewayMock.createBrcodeTransaction(any())).thenReturn(defaultTransactionId)
            `when`(transactionGatewayMock.getTransaction(defaultTransactionId)).thenReturn(defaultTransaction)
            `when`(transactionGatewayMock.getBankingPartnerPaymentByProduct(
                defaultTransactionOrigin.sourceProductReferenceId, defaultTransactionOrigin.sourceProductReferenceName
            )).thenReturn(null)
            `when`(payBrcodeGatewayMock.payBrcode(defaultBrcode, defaultBrcodeInspected.pixBeneficiary, defaultTransactionOrigin)).thenReturn(null)

            // When
            val exception = assertThrows<InternalErrorException> {
                payBrcodeUseCase.execute(defaultBrcode, defaultTransactionOrigin)
            }

            // Then
            assertEquals(PayBrcodeErrorEnum.PAYMENT_ERROR.toString(), exception.message)
            verify(brcodeInspectionGatewayMock, times(1)).inspectBrcode(defaultBrcode)
            verify(transactionGatewayMock, times(1)).getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )
            verify(transactionGatewayMock, times(1)).createBrcodeTransaction(any())
            verify(transactionGatewayMock, times(1)).getTransaction(defaultTransactionId)
            verify(transactionGatewayMock, times(1)).getBankingPartnerPaymentByProduct(
                defaultTransactionOrigin.sourceProductReferenceId, defaultTransactionOrigin.sourceProductReferenceName
            )
            verify(payBrcodeGatewayMock, times(1)).payBrcode(defaultBrcode, defaultBrcodeInspected.pixBeneficiary, defaultTransactionOrigin)
            verify(transactionGatewayMock, never()).updateTransaction(anyString(), any(), anyString())
        }

        @Test
        fun `Should throw if get banking partner payment fails`() {
            // Given
            `when`(brcodeInspectionGatewayMock.inspectBrcode(defaultBrcode)).thenReturn(InspectBrcodeOutput(defaultBrcodeInspected))
            `when`(transactionGatewayMock.getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )).thenReturn(null)
            `when`(transactionGatewayMock.createBrcodeTransaction(any())).thenReturn(defaultTransactionId)
            `when`(transactionGatewayMock.getTransaction(defaultTransactionId)).thenReturn(defaultTransaction)
            `when`(transactionGatewayMock.getBankingPartnerPaymentByProduct(
                defaultTransactionOrigin.sourceProductReferenceId, defaultTransactionOrigin.sourceProductReferenceName)
            ).thenAnswer { throw Exception("GetBankingPartnerPaymentFailed") }

            // When
            val exception = assertThrows<Exception> {
                payBrcodeUseCase.execute(defaultBrcode, defaultTransactionOrigin)
            }

            // Then
            assertEquals("GetBankingPartnerPaymentFailed", exception.message)
            verify(brcodeInspectionGatewayMock, times(1)).inspectBrcode(defaultBrcode)
            verify(transactionGatewayMock, times(1)).getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )
            verify(transactionGatewayMock, times(1)).createBrcodeTransaction(any())
            verify(transactionGatewayMock, times(1)).getTransaction(defaultTransactionId)
            verify(transactionGatewayMock, times(1)).getBankingPartnerPaymentByProduct(
                defaultTransactionOrigin.sourceProductReferenceId, defaultTransactionOrigin.sourceProductReferenceName
            )
            verify(payBrcodeGatewayMock, never()).payBrcode(defaultBrcode, defaultBrcodeInspected.pixBeneficiary, defaultTransactionOrigin)
            verify(transactionGatewayMock, never()).updateTransaction(anyString(), any(), anyString())
        }

        @Test
        fun `Should throw if transaction update does not succeed`() {
            // Given
            `when`(brcodeInspectionGatewayMock.inspectBrcode(defaultBrcode)).thenReturn(InspectBrcodeOutput(defaultBrcodeInspected))
            `when`(transactionGatewayMock.getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )).thenReturn(null)
            `when`(transactionGatewayMock.createBrcodeTransaction(any())).thenReturn(defaultTransactionId)
            `when`(transactionGatewayMock.getTransaction(defaultTransactionId)).thenReturn(defaultTransaction)
            `when`(transactionGatewayMock.getBankingPartnerPaymentByProduct(
                defaultTransactionOrigin.sourceProductReferenceId, defaultTransactionOrigin.sourceProductReferenceName
            )).thenReturn(null)
            `when`(payBrcodeGatewayMock.payBrcode(defaultBrcode, defaultBrcodeInspected.pixBeneficiary, defaultTransactionOrigin)).thenReturn(defaultBankingPartnerPaymentDto.id)
            `when`(transactionGatewayMock.updateTransaction(defaultTransactionId, TransactionStatusEnum.CREATED, defaultBankingPartnerPaymentDto.id)).thenReturn(false)

            // When
            val exception = assertThrows<InternalErrorException> {
                payBrcodeUseCase.execute(defaultBrcode, defaultTransactionOrigin)
            }

            // Then
            assertEquals(PayBrcodeErrorEnum.TRANSACTION_UPDATE_ERROR.toString(), exception.message)
            verify(brcodeInspectionGatewayMock, times(1)).inspectBrcode(defaultBrcode)
            verify(transactionGatewayMock, times(1)).getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )
            verify(transactionGatewayMock, times(1)).createBrcodeTransaction(any())
            verify(transactionGatewayMock, times(1)).getTransaction(defaultTransactionId)
            verify(transactionGatewayMock, times(1)).getBankingPartnerPaymentByProduct(
                defaultTransactionOrigin.sourceProductReferenceId, defaultTransactionOrigin.sourceProductReferenceName
            )
            verify(payBrcodeGatewayMock, times(1)).payBrcode(defaultBrcode, defaultBrcodeInspected.pixBeneficiary, defaultTransactionOrigin)
            verify(transactionGatewayMock, times(1)).updateTransaction(defaultTransactionId, TransactionStatusEnum.CREATED, defaultBankingPartnerPaymentDto.id)
        }
    }

    @Nested
    @DisplayName("When transaction exists but without a banking partner payment")
    inner class WhenTransactionExistsWithoutAPayment {
        @Test
        fun `Should pay brcode`() {
            // Given
            `when`(brcodeInspectionGatewayMock.inspectBrcode(defaultBrcode)).thenReturn(InspectBrcodeOutput(defaultBrcodeInspected))
            `when`(transactionGatewayMock.getTransaction(defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId)).thenReturn(defaultTransaction)
            `when`(transactionGatewayMock.getBankingPartnerPaymentByProduct(
                defaultTransactionOrigin.sourceProductReferenceId, defaultTransactionOrigin.sourceProductReferenceName
            )).thenReturn(null)
            `when`(payBrcodeGatewayMock.payBrcode(defaultBrcode, defaultBrcodeInspected.pixBeneficiary, defaultTransactionOrigin)).thenReturn(defaultBankingPartnerPaymentDto.id)
            `when`(transactionGatewayMock.updateTransaction(defaultTransactionId, TransactionStatusEnum.CREATED, defaultBankingPartnerPaymentDto.id)).thenReturn(true)

            // When
            val result = payBrcodeUseCase.execute(defaultBrcode, defaultTransactionOrigin)

            // Then
            assertEquals(defaultTransaction, result)
            verify(brcodeInspectionGatewayMock, times(1)).inspectBrcode(defaultBrcode)
            verify(transactionGatewayMock, times(1)).getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )
            verify(transactionGatewayMock, never()).createBrcodeTransaction(any())
            verify(transactionGatewayMock, never()).getTransaction(defaultTransactionId)
            verify(transactionGatewayMock, times(1)).getBankingPartnerPaymentByProduct(
                defaultTransactionOrigin.sourceProductReferenceId, defaultTransactionOrigin.sourceProductReferenceName
            )
            verify(payBrcodeGatewayMock, times(1)).payBrcode(defaultBrcode, defaultBrcodeInspected.pixBeneficiary, defaultTransactionOrigin)
            verify(transactionGatewayMock, times(1)).updateTransaction(defaultTransactionId, TransactionStatusEnum.CREATED, defaultBankingPartnerPaymentDto.id)
        }

        @Test
        fun `Should throw if brcode payment fails`() {
            // Given
            `when`(brcodeInspectionGatewayMock.inspectBrcode(defaultBrcode)).thenReturn(InspectBrcodeOutput(defaultBrcodeInspected))
            `when`(transactionGatewayMock.getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )).thenReturn(defaultTransaction)
            `when`(transactionGatewayMock.getBankingPartnerPaymentByProduct(defaultTransactionOrigin.sourceProductReferenceId, defaultTransactionOrigin.sourceProductReferenceName)).thenReturn(null)
            `when`(payBrcodeGatewayMock.payBrcode(defaultBrcode, defaultBrcodeInspected.pixBeneficiary, defaultTransactionOrigin))
                .thenAnswer { throw Exception("PayBrcodeFailed") }

            // When
            val exception = assertThrows<Exception> {
                payBrcodeUseCase.execute(defaultBrcode, defaultTransactionOrigin)
            }

            // Then
            assertEquals("PayBrcodeFailed", exception.message)
            verify(brcodeInspectionGatewayMock, times(1)).inspectBrcode(defaultBrcode)
            verify(transactionGatewayMock, times(1)).getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )
            verify(transactionGatewayMock, never()).createBrcodeTransaction(any())
            verify(transactionGatewayMock, never()).getTransaction(defaultTransactionId)
            verify(transactionGatewayMock, times(1)).getBankingPartnerPaymentByProduct(defaultTransactionOrigin.sourceProductReferenceId, defaultTransactionOrigin.sourceProductReferenceName)
            verify(payBrcodeGatewayMock, times(1)).payBrcode(defaultBrcode, defaultBrcodeInspected.pixBeneficiary, defaultTransactionOrigin)
            verify(transactionGatewayMock, never()).updateTransaction(defaultTransactionId, TransactionStatusEnum.CREATED, defaultBankingPartnerPaymentDto.id)
        }
    }

    @Nested
    @DisplayName("When transaction exists and has a banking partner payment")
    inner class WhenTransactionExistsWithAPayment {
        @Test
        fun `Should update transaction if partnerExternalId is null`() {
            // Given
            val transaction = TransactionBuilder.build(defaultTransactionId).copy(partnerExternalId = null)
            `when`(brcodeInspectionGatewayMock.inspectBrcode(defaultBrcode)).thenReturn(InspectBrcodeOutput(defaultBrcodeInspected))
            `when`(transactionGatewayMock.getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )).thenReturn(transaction)
            `when`(transactionGatewayMock.getBankingPartnerPaymentByProduct(
                defaultTransactionOrigin.sourceProductReferenceId, defaultTransactionOrigin.sourceProductReferenceName
            )).thenReturn(defaultBankingPartnerPaymentDto)
            `when`(transactionGatewayMock.updateTransaction(transaction.id, defaultBankingPartnerPaymentStatus, defaultBankingPartnerPaymentDto.id))
                .thenReturn(true)

            // When
            val result = payBrcodeUseCase.execute(defaultBrcode, defaultTransactionOrigin)

            // Then
            assertEquals(transaction, result)
            verify(brcodeInspectionGatewayMock, times(1)).inspectBrcode(defaultBrcode)
            verify(transactionGatewayMock, times(1)).getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )
            verify(transactionGatewayMock, never()).createBrcodeTransaction(any())
            verify(transactionGatewayMock, never()).getTransaction(defaultTransactionId)
            verify(transactionGatewayMock, times(1)).getBankingPartnerPaymentByProduct(
                defaultTransactionOrigin.sourceProductReferenceId, defaultTransactionOrigin.sourceProductReferenceName
            )
            verify(payBrcodeGatewayMock, never()).payBrcode(defaultBrcode, defaultBrcodeInspected.pixBeneficiary, defaultTransactionOrigin)
            verify(transactionGatewayMock, times(1)).updateTransaction(transaction.id, defaultBankingPartnerPaymentStatus, defaultBankingPartnerPaymentDto.id)
        }

        @Test
        fun `Should throw if transaction update fails`() {
            // Given
            val transaction = TransactionBuilder.build(defaultTransactionId).copy(partnerExternalId = null)
            `when`(brcodeInspectionGatewayMock.inspectBrcode(defaultBrcode)).thenReturn(InspectBrcodeOutput(defaultBrcodeInspected))
            `when`(transactionGatewayMock.getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )).thenReturn(transaction)
            `when`(transactionGatewayMock.getBankingPartnerPaymentByProduct(defaultTransactionOrigin.sourceProductReferenceId, defaultTransactionOrigin.sourceProductReferenceName)).thenReturn(defaultBankingPartnerPaymentDto)
            `when`(transactionGatewayMock.updateTransaction(transaction.id, defaultBankingPartnerPaymentStatus, defaultBankingPartnerPaymentDto.id))
                .thenAnswer { throw Exception("UpdateTransactionFailed") }

            // When
            val exception = assertThrows<Exception> {
                payBrcodeUseCase.execute(defaultBrcode, defaultTransactionOrigin)
            }

            // Then
            assertEquals("UpdateTransactionFailed", exception.message)
            verify(brcodeInspectionGatewayMock, times(1)).inspectBrcode(defaultBrcode)
            verify(transactionGatewayMock, times(1)).getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )
            verify(transactionGatewayMock, never()).createBrcodeTransaction(any())
            verify(transactionGatewayMock, never()).getTransaction(defaultTransactionId)
            verify(transactionGatewayMock, times(1)).getBankingPartnerPaymentByProduct(
                defaultTransactionOrigin.sourceProductReferenceId, defaultTransactionOrigin.sourceProductReferenceName
            )
            verify(payBrcodeGatewayMock, never()).payBrcode(defaultBrcode, defaultBrcodeInspected.pixBeneficiary, defaultTransactionOrigin)
            verify(transactionGatewayMock, times(1)).updateTransaction(transaction.id, defaultBankingPartnerPaymentStatus, defaultBankingPartnerPaymentDto.id)
        }

        @Test
        fun `Should do nothing if partnerExternalId is set`() {
            // Given
            `when`(brcodeInspectionGatewayMock.inspectBrcode(defaultBrcode)).thenReturn(InspectBrcodeOutput(defaultBrcodeInspected))
            `when`(transactionGatewayMock.getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )).thenReturn(defaultTransaction)
            `when`(transactionGatewayMock.getBankingPartnerPaymentByProduct(
                defaultTransactionOrigin.sourceProductReferenceId, defaultTransactionOrigin.sourceProductReferenceName
            )).thenReturn(defaultBankingPartnerPaymentDto)

            // When
            val result = payBrcodeUseCase.execute(defaultBrcode, defaultTransactionOrigin)

            // Then
            assertEquals(defaultTransaction, result)
            verify(brcodeInspectionGatewayMock, times(1)).inspectBrcode(defaultBrcode)
            verify(transactionGatewayMock, times(1)).getTransaction(
                defaultTransactionOrigin.sourceProductReferenceName, defaultTransactionOrigin.sourceProductReferenceId
            )
            verify(transactionGatewayMock, never()).createBrcodeTransaction(any())
            verify(transactionGatewayMock, never()).getTransaction(defaultTransactionId)
            verify(transactionGatewayMock, times(1)).getBankingPartnerPaymentByProduct(
                defaultTransactionOrigin.sourceProductReferenceId, defaultTransactionOrigin.sourceProductReferenceName
            )
            verify(payBrcodeGatewayMock, never()).payBrcode(defaultBrcode, defaultBrcodeInspected.pixBeneficiary, defaultTransactionOrigin)
            verify(transactionGatewayMock, never()).updateTransaction(defaultTransactionId, TransactionStatusEnum.CREATED, defaultBankingPartnerPaymentDto.id)
        }
    }
}
