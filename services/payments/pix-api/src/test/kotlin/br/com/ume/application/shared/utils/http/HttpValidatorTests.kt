package br.com.ume.application.shared.utils.http

import br.com.ume.application.shared.resources.httpSuccessCodes
import io.micronaut.http.HttpStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class HttpValidatorTests {

    @Nested
    @DisplayName("isSuccessHttpCode()")
    inner class IsSuccessStatusCode {

        @Test
        fun `Should return true for success status code`() {
            httpSuccessCodes.forEach { assertEquals(true, isSuccessHttpCode(it.code))}
        }

        @Test
        fun `Should return false for any other status code`() {
            assertEquals(false, isSuccessHttpCode(HttpStatus.BAD_REQUEST.code))
            assertEquals(false, isSuccessHttpCode(HttpStatus.UNAUTHORIZED.code))
            assertEquals(false, isSuccessHttpCode(HttpStatus.FORBIDDEN.code))
            assertEquals(false, isSuccessHttpCode(HttpStatus.NOT_FOUND.code))
            assertEquals(false, isSuccessHttpCode(HttpStatus.INTERNAL_SERVER_ERROR.code))
        }
    }
}