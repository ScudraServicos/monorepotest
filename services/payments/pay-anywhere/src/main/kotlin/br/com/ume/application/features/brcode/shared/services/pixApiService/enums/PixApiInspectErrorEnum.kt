package br.com.ume.application.features.brcode.shared.services.pixApiService.enums

enum class PixApiInspectErrorEnum {
    BRCODE_WITHOUT_URL_AND_KEY,
    BANKING_PARTNER_INVALID_QR_CODE,
    BANKING_PARTNER_ERROR,
    BANKING_PARTNER_INVALID_DICT,
    BANKING_PARTNER_DICT_ERROR,
    BANKING_PARTNER_BENEFICIARY_PARSING_ERROR,
    BRCODE_PAYLOAD_NOT_AVAILABLE,
    BRCODE_PAYLOAD_NOT_VALID,
    BRCODE_PAYLOAD_DECODING_ERROR,
    BRCODE_PAYLOAD_ERROR,
    PIX_KEY_NOT_FOUND,
    UNKNOWN_ERROR
}