package br.com.ume.application.features.brcode.inspectBrcode.useCase.enums

enum class InspectBrcodeErrorEnum {
    INVALID,
    ERROR,
    NATURAL_PERSON_BENEFICIARY,
    STATIC_PIX_WITHOUT_VALUE,
    PIX_WITHDRAW,
    PIX_CHANGE,
    ALLOW_ALTERATION,
    MINIMUM_VALUE,
    LEGAL_PERSON_BENEFICIARY_IN_BLOCK_LIST,
    BRCODE_NOT_ACTIVE,
    PERMISSION_DENIED,
}
