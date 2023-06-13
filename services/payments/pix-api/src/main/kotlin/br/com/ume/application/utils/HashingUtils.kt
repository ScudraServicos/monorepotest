package br.com.ume.application.utils

import java.math.BigInteger
import java.security.MessageDigest

abstract class HashingUtils {
    companion object {
        fun toSha1(text: String): String {
            val md = MessageDigest.getInstance("SHA-1")
            val messageDigest = md.digest(text.toByteArray())
            val number = BigInteger(1, messageDigest)
            return number.toString(16)
        }
    }
}