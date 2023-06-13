package br.com.ume.api.exceptions

import br.com.ume.api.exceptions.base.ApiError
import br.com.ume.api.exceptions.types.*
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error

@Controller
class GlobalExceptionController {
    @Error(global = true, exception = NotFoundException::class)
    fun notFoundError(exception: NotFoundException): HttpResponse<ApiError> {
        val message = exception.message ?: "Not found"
        return HttpResponse.notFound(ApiError(message))
    }

    @Error(global = true, exception = BusinessRuleException::class)
    fun businessRuleError(exception: BusinessRuleException): HttpResponse<ApiError> {
        val message = exception.message ?: "Error"
        return HttpResponse.badRequest(ApiError(message))
    }

    @Error(global = true, exception = InternalErrorException::class)
    fun internalError(exception: InternalErrorException): HttpResponse<ApiError> {
        val message = exception.message ?: "Error"
        return HttpResponse.serverError(ApiError(message))
    }

    @Error(global = true, exception = ExternalApiValidationException::class)
    fun externalApiValidationError(exception: ExternalApiValidationException): HttpResponse<ApiError> {
        val message = exception.message ?: "External api validation error"
        return HttpResponse.badRequest(ApiError(message))
    }

    @Error(global = true, exception = ExternalApiErrorException::class)
    fun externalApiError(exception: ExternalApiErrorException): HttpResponse<ApiError> {
        val message = exception.message ?: "External api error"
        return HttpResponse.serverError(ApiError(message))
    }
}