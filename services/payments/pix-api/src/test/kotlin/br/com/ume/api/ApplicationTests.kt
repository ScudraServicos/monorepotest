package br.com.ume.api

import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import jakarta.inject.Inject

@MicronautTest
class ApplicationTests {
    @Inject
    lateinit var application: EmbeddedApplication<*>

    @Test
    fun `Test application bootstrap`() {
        Assertions.assertTrue(application.isRunning)
    }

}
