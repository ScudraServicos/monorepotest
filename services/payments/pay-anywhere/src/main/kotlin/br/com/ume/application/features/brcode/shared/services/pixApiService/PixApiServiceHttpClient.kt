package br.com.ume.application.features.brcode.shared.services.pixApiService

import jakarta.inject.Singleton
import java.net.http.HttpClient
import java.time.Duration


@Singleton
class PixApiServiceHttpClient {
    val client: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build()
}