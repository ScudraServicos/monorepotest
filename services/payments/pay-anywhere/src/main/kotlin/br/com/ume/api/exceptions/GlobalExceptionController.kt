package br.com.ume.api.exceptions

import br.com.ume.api.exceptions.base.ApiError
import br.com.ume.api.exceptions.types.BusinessRuleException
import br.com.ume.api.exceptions.types.ForbiddenException
import br.com.ume.api.exceptions.types.InternalErrorException
import br.com.ume.api.exceptions.types.NotFoundException
import br.com.ume.application.features.brcode.shared.services.brcodeValidation.exceptions.BrcodeValidationException
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
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

    @Error(global = true, exception = ForbiddenException::class)
    fun forbiddenError(): HttpResponse<ApiError> {
        return HttpResponse.status(HttpStatus.FORBIDDEN)
    }

    @Error(global = true, exception = InternalErrorException::class)
    fun internalError(exception: InternalErrorException): HttpResponse<ApiError> {
        val message = exception.message ?: "Error"
        return HttpResponse.serverError(ApiError(message))
    }

    @Error(global = true, exception = BrcodeValidationException::class)
    fun brcodeValidationError(exception: BrcodeValidationException): HttpResponse<ApiError> {
        val message = exception.message ?: "Error"
        return HttpResponse.badRequest(ApiError(message))
    }
}