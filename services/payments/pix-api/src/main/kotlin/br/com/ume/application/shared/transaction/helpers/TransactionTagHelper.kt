package br.com.ume.application.shared.transaction.helpers

abstract class TransactionTagHelper {
    companion object {
        private const val delimiter = "_"

        fun getTagFromProduct(productId: String, sourceProduct: String): String {
            return "${productId}${delimiter}${sourceProduct}"
        }
    }
}