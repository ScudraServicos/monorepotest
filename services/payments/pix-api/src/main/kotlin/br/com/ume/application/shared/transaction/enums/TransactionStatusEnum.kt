package br.com.ume.application.shared.transaction.enums

enum class TransactionStatusEnum {
    PENDING_CREATION,
    CREATED,
    PROCESSING,
    CANCELED,
    FAILED,
    REFUNDED,
    SUCCESS;

    companion object {
        fun fromBankingPartnerStatus(status: String): TransactionStatusEnum? {
            return when (status.uppercase()) {
                "CREATED" -> CREATED
                "PROCESSING" -> PROCESSING
                "SUCCESS" -> SUCCESS
                "CANCELED" -> CANCELED
                "FAILED" -> FAILED
                else -> null
            }
        }

        fun isStatusFinal(status: TransactionStatusEnum): Boolean {
            val finalStatus = setOf(CANCELED, FAILED, SUCCESS)
            return finalStatus.contains(status)
        }
    }
}