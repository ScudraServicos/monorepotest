package br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService

import br.com.ume.application.features.brcode.shared.brcodeInspected.enums.InspectBrcodeErrorEnum
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.dtos.PixBeneficiaryDto
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.dtos.GetPixBeneficiaryOutput
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.dtos.PixBeneficiaryBankingAccount
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.enums.PixBeneficiaryType
import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.errors.BankingPartnerParsingException
import br.com.ume.application.features.brcode.shared.brcodeInspected.wrappers.bankingPartner.BankingPartnerWrapper
import br.com.ume.application.shared.logging.gcp.JsonLogBuilder
import br.com.ume.application.shared.logging.gcp.LoggerFactory
import com.starkbank.DictKey
import io.micronaut.runtime.http.scope.RequestScope
import java.util.logging.Logger
import com.starkbank.error.InputErrors as BankingPartnerInputErrors
import com.starkbank.error.InternalServerError as BankingPartnerInternalServerError

@RequestScope
class PixBeneficiaryServiceImpl(
    private val bankingPartner: BankingPartnerWrapper
) : PixBeneficiaryService {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(PixBeneficiaryServiceImpl::class.java.name)
    }

    override fun getBeneficiary(pixKey: String): GetPixBeneficiaryOutput {
        return try {
            val dictKey = bankingPartner.getPixBeneficiary(pixKey)

            GetPixBeneficiaryOutput(parseBeneficiaryFromBankingPartner(dictKey))
        } catch (ex: BankingPartnerInputErrors) {
            logException(InspectBrcodeErrorEnum.BANKING_PARTNER_INVALID_DICT.toString(), pixKey, ex)

            GetPixBeneficiaryOutput(error = InspectBrcodeErrorEnum.BANKING_PARTNER_INVALID_DICT)
        } catch (ex: BankingPartnerInternalServerError) {
            logException(InspectBrcodeErrorEnum.BANKING_PARTNER_DICT_ERROR.toString(), pixKey, ex)

            GetPixBeneficiaryOutput(error = InspectBrcodeErrorEnum.BANKING_PARTNER_DICT_ERROR)
        } catch (ex: BankingPartnerParsingException) {
            logException(InspectBrcodeErrorEnum.BANKING_PARTNER_BENEFICIARY_PARSING_ERROR.toString(), pixKey, ex)

            GetPixBeneficiaryOutput(error = InspectBrcodeErrorEnum.BANKING_PARTNER_BENEFICIARY_PARSING_ERROR)
        }
    }

    private fun parseBeneficiaryFromBankingPartner(dictKey: DictKey): PixBeneficiaryDto {
        val type = parseBeneficiaryType(dictKey)

        return PixBeneficiaryDto(
            name = dictKey.name,
            document = dictKey.taxId,
            type = type,
            businessName = null,
            pixKey = dictKey.id,
            bankingAccount = buildPixBeneficiaryBankingAccount(dictKey)
        )
    }

    private fun parseBeneficiaryType(dictKey: DictKey): PixBeneficiaryType {
        return when (dictKey.ownerType) {
            "naturalPerson", "individual" -> PixBeneficiaryType.NATURAL_PERSON
            "legalPerson", "business" -> PixBeneficiaryType.LEGAL_PERSON
            else -> throw BankingPartnerParsingException("parseBeneficiaryType")
        }
    }

    private fun buildPixBeneficiaryBankingAccount(dictKey: DictKey): PixBeneficiaryBankingAccount {
        return PixBeneficiaryBankingAccount(
            bankName = dictKey.bankName,
            bankCode = dictKey.ispb,
            branchCode = dictKey.branchCode,
            accountNumber = dictKey.accountNumber,
            accountType = dictKey.accountType
        )
    }

    private fun logException(message: String, pixKey: String, exception: Exception) {
        log.severe(JsonLogBuilder.build(object {
            val message = message
            val pixKey = pixKey
            val exception = exception
        }))
    }
}