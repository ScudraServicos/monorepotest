package br.com.ume.application.brcode.shared.builders

import br.com.ume.application.brcode.shared.testBuilders.BrcodePayloadBuilder
import br.com.ume.application.brcode.shared.testBuilders.BrcodePreviewBuilder
import br.com.ume.application.brcode.shared.testBuilders.DecodedBrcodeBuilder
import br.com.ume.application.brcode.shared.testBuilders.PixBeneficiaryBuilder
import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.PixStatusEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.PixTypeEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.builders.BrcodeInspectedBuilder
import br.com.ume.application.features.brcode.shared.brcodeInspected.domain.BrcodeInspected
import br.com.ume.application.features.brcode.shared.brcodeInspected.domain.BrcodeInspectedWithdrawInfo
import br.com.ume.application.features.brcode.shared.brcodeInspected.domain.BrcodeInspectedChangeInfo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class BrcodeInspectedBuilderTests {
    @Nested
    @DisplayName("buildOutput()")
    inner class BuildOutput {
        @Test
        fun `Should return output for static brcode`() {
            // Given
            val decodedBrcode = DecodedBrcodeBuilder.buildStaticBrcode()
            val brcodePreview = BrcodePreviewBuilder.build()
            val brcodePayload = null
            val pixBeneficiary = PixBeneficiaryBuilder.buildLegalPerson()

            // When
            val inspectBrcodeOutput = BrcodeInspectedBuilder.buildOutput(decodedBrcode, brcodePreview, brcodePayload, pixBeneficiary)

            // Then
            val expectedResult = BrcodeInspected(
                status = PixStatusEnum.ACTIVE,
                pixType = PixTypeEnum.STATIC,
                value = 25.0,
                allowAlteration = false,
                txId = null,
                createdAt = null,
                presentedAt = null,
                expiresAt = null,
                dueDate = null,
                withdrawInfo = null,
                changeInfo = null,
                pixBeneficiary = pixBeneficiary
            )
            assertEquals(expectedResult, inspectBrcodeOutput)
        }

        @Test
        fun `Should return output with dates for dynamic brcode`() {
            // Given
            val decodedBrcode = DecodedBrcodeBuilder.buildDynamicBrcode()
            val brcodePreview = BrcodePreviewBuilder.build()
            val brcodePayload = BrcodePayloadBuilder.build()
            val pixBeneficiary = PixBeneficiaryBuilder.buildLegalPerson()

            // When
            val inspectBrcodeOutput = BrcodeInspectedBuilder.buildOutput(decodedBrcode, brcodePreview, brcodePayload, pixBeneficiary)

            // Then
            val expectedResult = BrcodeInspected(
                status = PixStatusEnum.ACTIVE,
                pixType = PixTypeEnum.DYNAMIC,
                value = 25.0,
                allowAlteration = false,
                txId = null,
                createdAt = LocalDateTime.parse("2023-01-30T14:59:15"),
                presentedAt = LocalDateTime.parse("2023-01-30T14:59:45"),
                expiresAt = LocalDateTime.parse("2023-01-31T14:59:15"),
                dueDate = LocalDate.parse("2023-02-15"),
                withdrawInfo = null,
                changeInfo = null,
                pixBeneficiary = pixBeneficiary
            )
            assertEquals(expectedResult, inspectBrcodeOutput)
        }

        @Test
        fun `Should return output with troco for dynamic brcode`() {
            // Given
            val decodedBrcode = DecodedBrcodeBuilder.buildDynamicBrcode()
            val brcodePreview = BrcodePreviewBuilder.build()
            val brcodePayload = BrcodePayloadBuilder.buildWithTroco()
            val pixBeneficiary = PixBeneficiaryBuilder.buildLegalPerson()

            // When
            val inspectBrcodeOutput = BrcodeInspectedBuilder.buildOutput(decodedBrcode, brcodePreview, brcodePayload, pixBeneficiary)

            // Then
            val expectedResult = BrcodeInspected(
                status = PixStatusEnum.ACTIVE,
                pixType = PixTypeEnum.DYNAMIC,
                value = 25.0,
                allowAlteration = false,
                txId = null,
                createdAt = LocalDateTime.parse("2023-01-30T14:59:15"),
                presentedAt = LocalDateTime.parse("2023-01-30T14:59:45"),
                expiresAt = LocalDateTime.parse("2023-01-31T14:59:15"),
                dueDate = LocalDate.parse("2023-02-15"),
                withdrawInfo = null,
                changeInfo = BrcodeInspectedChangeInfo(value = 10.0),
                pixBeneficiary = pixBeneficiary
            )
            assertEquals(expectedResult, inspectBrcodeOutput)
        }

        @Test
        fun `Should return output with saque for dynamic brcode`() {
            // Given
            val decodedBrcode = DecodedBrcodeBuilder.buildDynamicBrcode()
            val brcodePreview = BrcodePreviewBuilder.build()
            val brcodePayload = BrcodePayloadBuilder.buildWithSaque()
            val pixBeneficiary = PixBeneficiaryBuilder.buildLegalPerson()

            // When
            val inspectBrcodeOutput = BrcodeInspectedBuilder.buildOutput(decodedBrcode, brcodePreview, brcodePayload, pixBeneficiary)

            // Then
            val expectedResult = BrcodeInspected(
                status = PixStatusEnum.ACTIVE,
                pixType = PixTypeEnum.DYNAMIC,
                value = 25.0,
                allowAlteration = false,
                txId = null,
                createdAt = LocalDateTime.parse("2023-01-30T14:59:15"),
                presentedAt = LocalDateTime.parse("2023-01-30T14:59:45"),
                expiresAt = LocalDateTime.parse("2023-01-31T14:59:15"),
                dueDate = LocalDate.parse("2023-02-15"),
                changeInfo = null,
                withdrawInfo = BrcodeInspectedWithdrawInfo(value = 15.0),
                pixBeneficiary = pixBeneficiary
            )
            assertEquals(expectedResult, inspectBrcodeOutput)
        }
    }
}