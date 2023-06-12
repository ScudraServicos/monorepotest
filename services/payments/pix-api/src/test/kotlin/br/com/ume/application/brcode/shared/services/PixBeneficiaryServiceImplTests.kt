package br.com.ume.application.brcode.shared.services

import br.com.ume.application.brcode.shared.testBuilders.BankingPartnerDictKeyBuilder
import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.PixBeneficiaryService
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.PixBeneficiaryServiceImpl
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.dtos.GetPixBeneficiaryOutput
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.dtos.PixBeneficiaryBankingAccount
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.dtos.PixBeneficiaryDto
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.enums.PixBeneficiaryType
import br.com.ume.application.features.brcode.shared.brcodeInspected.wrappers.bankingPartner.BankingPartnerWrapper
import com.starkbank.error.InputErrors as BankingPartnerInputErrors
import com.starkbank.error.InternalServerError as BankingPartnerInternalServerError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito

class PixBeneficiaryServiceImplTests {
    private lateinit var bankingPartner: BankingPartnerWrapper
    private lateinit var pixBeneficiaryService: PixBeneficiaryService

    @BeforeEach()
    fun setUp() {
        bankingPartner = Mockito.mock(BankingPartnerWrapper::class.java)
        pixBeneficiaryService = PixBeneficiaryServiceImpl(bankingPartner)
    }

    @Nested
    @DisplayName("getBeneficiary()")
    inner class GetBeneficiary {
        @Test
        fun `Should return pix beneficiary`() {
            // Given
            val pixKey = "123"
            val bankingPartnerPixBeneficiary = BankingPartnerDictKeyBuilder.build()
            Mockito.`when`(bankingPartner.getPixBeneficiary(pixKey)).thenReturn(bankingPartnerPixBeneficiary)

            // When
            val result = pixBeneficiaryService.getBeneficiary(pixKey)

            // Then
            val expectedResult = GetPixBeneficiaryOutput(
                PixBeneficiaryDto(
                    name = "Red Forman",
                    document = "***.456.789-**",
                    type = PixBeneficiaryType.NATURAL_PERSON,
                    businessName = null,
                    pixKey = "1aa1aaaa-a11a-1111-a111-1a1aa111aaaa",
                    bankingAccount = PixBeneficiaryBankingAccount(
                        bankName = "Stark Bank",
                        bankCode = "93641612",
                        branchCode = "4506",
                        accountNumber = "2924145735530665",
                        accountType = "saving"
                    )
                )
            )
            assertEquals(expectedResult, result)
        }

        @Test
        fun `Should return error if dict is invalid`() {
            // Given
            val pixKey = "123"
            Mockito.`when`(bankingPartner.getPixBeneficiary(anyString())).thenThrow(
                BankingPartnerInputErrors::class.java
            )

            // When
            val result = pixBeneficiaryService.getBeneficiary(pixKey)

            // Then
            val expectedResult = GetPixBeneficiaryOutput(
                error = InspectBrcodeErrorEnum.BANKING_PARTNER_INVALID_DICT
            )
            assertEquals(expectedResult, result)
        }

        @Test
        fun `Should return error if dict has error`() {
            // Given
            val pixKey = "123"
            Mockito.`when`(bankingPartner.getPixBeneficiary(anyString())).thenThrow(
                BankingPartnerInternalServerError::class.java
            )

            // When
            val result = pixBeneficiaryService.getBeneficiary(pixKey)

            // Then
            val expectedResult = GetPixBeneficiaryOutput(
                error = InspectBrcodeErrorEnum.BANKING_PARTNER_DICT_ERROR
            )
            assertEquals(expectedResult, result)
        }

        @Test
        fun `Should return error if has error on parsing beneficiary type`() {
            // Given
            val pixKey = "123"
            val bankingPartnerPixBeneficiary = BankingPartnerDictKeyBuilder.build()
            bankingPartnerPixBeneficiary.ownerType="invalid"
            Mockito.`when`(bankingPartner.getPixBeneficiary(anyString())).thenReturn(bankingPartnerPixBeneficiary)

            // When
            val result = pixBeneficiaryService.getBeneficiary(pixKey)

            // Then
            val expectedResult = GetPixBeneficiaryOutput(
                error = InspectBrcodeErrorEnum.BANKING_PARTNER_BENEFICIARY_PARSING_ERROR
            )
            assertEquals(expectedResult, result)
        }
    }
}