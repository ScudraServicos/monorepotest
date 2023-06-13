package br.com.ume.application.shared.utils.http

import jakarta.inject.Singleton
import java.net.http.HttpClient
import java.time.Duration

@Singleton
class HttpBuilder {
    fun buildClient(timeoutInSec: Long = 10): HttpClient {
        return HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(timeoutInSec))
            .build()
    }
}