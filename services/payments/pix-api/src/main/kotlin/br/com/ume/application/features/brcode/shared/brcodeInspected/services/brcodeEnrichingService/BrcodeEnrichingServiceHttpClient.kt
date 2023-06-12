package br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeEnrichingService

import jakarta.inject.Singleton
import java.net.http.HttpClient
import java.time.Duration

@Singleton
class BrcodeEnrichingServiceHttpClient {
    val client: HttpClient =  HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build()
}