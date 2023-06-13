package br.com.ume.libs.logging.gcp

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.inject.Singleton

@Singleton
object CustomSerializer {
    fun <T> serialize(value: T): String = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .writeValueAsString(value)
}