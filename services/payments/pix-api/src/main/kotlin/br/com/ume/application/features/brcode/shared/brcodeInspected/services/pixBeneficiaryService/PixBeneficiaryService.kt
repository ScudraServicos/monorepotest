package br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService

import br.com.ume.application.features.brcode.shared.brcodeInspected.services.pixBeneficiaryService.dtos.GetPixBeneficiaryOutput

interface PixBeneficiaryService {
    fun getBeneficiary(pixKey: String): GetPixBeneficiaryOutput
}