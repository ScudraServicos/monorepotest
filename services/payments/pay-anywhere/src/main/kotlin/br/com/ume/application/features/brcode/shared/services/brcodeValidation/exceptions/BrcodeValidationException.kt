package br.com.ume.application.features.brcode.shared.services.brcodeValidation.exceptions

import br.com.ume.application.features.brcode.inspectBrcode.useCase.enums.InspectBrcodeErrorEnum

class BrcodeValidationException(error: InspectBrcodeErrorEnum): Exception(error.toString()) {
}