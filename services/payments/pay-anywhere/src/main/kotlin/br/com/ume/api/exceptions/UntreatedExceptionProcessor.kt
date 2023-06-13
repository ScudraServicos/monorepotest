package br.com.ume.api.exceptions

import br.com.ume.api.exceptions.base.ApiError
import br.com.ume.libs.logging.gcp.JsonLogBuilder
import br.com.ume.libs.logging.gcp.LoggerFactory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.server.exceptions.response.ErrorContext
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor
import io.micronaut.http.server.exceptions.response.HateoasErrorResponseProcessor
import jakarta.inject.Singleton
import java.util.logging.Logger

@Singleton
@Replaces(HateoasErrorResponseProcessor::class)
class UntreatedExceptionProcessor : ErrorResponseProcessor<ApiError> {
    companion object {
        private val log: Logger = LoggerFactory.buildStructuredLogger(UntreatedExceptionProcessor::class.java.name)
    }

    override fun processResponse(
        errorContext: ErrorContext,
        response: MutableHttpResponse<*>
    ): MutableHttpResponse<ApiError> {
        val error = errorContext.errors.joinToString { it.message }
        val rootCause: Throwable? = if (errorContext.rootCause.isPresent) errorContext.rootCause.get() else null
        log.severe(JsonLogBuilder.build(object {
            val message = "Unexpected error: $error"
            val stackTrace = rootCause?.stackTrace
        }))

        return if (response.code() in 400..499) {
            HttpResponse.serverError(ApiError("Bad request"))
                .contentType(MediaType.APPLICATION_JSON_TYPE)
                .status(response.status)
        } else {
            val internalServerError = ApiError("Internal server error")
            HttpResponse.serverError(internalServerError)
                .contentType(MediaType.APPLICATION_JSON_TYPE)
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}