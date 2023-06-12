package br.com.ume.api.filters

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@MicronautTest(environments = ["local"])
class ApiKeyFilterTests {

    @Test
    fun `Should return unauthorized when no ApiKey header sent`(spec: RequestSpecification) {
        // Given/Where
        val response = spec
            .`when`()
            .get("/route")

        // Then
        assertEquals(response.statusCode, 401)
    }

    @Test
    fun `Should return unauthorized when wrong ApiKey header sent`(spec: RequestSpecification) {
        // Given/Where
        val response = spec
            .given()
            .header("X-API-KEY", "INVALID")
            .`when`()
            .get("/route")

        // Then
        assertEquals(response.statusCode, 401)
    }

    @Test
    fun `Should return expected result when correct ApiKey header sent`(spec: RequestSpecification) {
        // Given/Where
        val response = spec
            .given()
            .header("X-API-KEY", "123")
            .`when`()
            .get("/route")

        // Then
        assertEquals(response.statusCode, 404)
    }

    @Test
    fun `Should return an expected result when requesting pubsub route without ApiKey`(spec: RequestSpecification) {
        // Given/Where
        val response = spec
            .given()
            .`when`()
            .get("/pubsub")

        // Then
        assertEquals(response.statusCode, 404)
    }
}