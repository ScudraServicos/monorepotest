package br.com.ume.application.shared.utils.http

import br.com.ume.application.shared.resources.httpSuccessCodes
import io.micronaut.http.HttpStatus

fun isSuccessHttpCode(code: Int): Boolean {
    return httpSuccessCodes.contains(HttpStatus.valueOf(code))
}