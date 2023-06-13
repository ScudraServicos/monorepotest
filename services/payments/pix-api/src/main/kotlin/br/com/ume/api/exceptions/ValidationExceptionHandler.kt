package br.com.ume.api.exceptions

import br.com.ume.api.exceptions.base.FieldError
import br.com.ume.api.exceptions.base.ValidationError
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.server.exceptions.ExceptionHandler
import io.micronaut.validation.exceptions.ConstraintExceptionHandler
import jakarta.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@Replaces(ConstraintExceptionHandler::class)
class ValidationExceptionHandler : ExceptionHandler<ConstraintViolationException, HttpResponse<*>> {
    override fun handle(request: HttpRequest<*>, exception: ConstraintViolationException): HttpResponse<*> {
        val validationErrors = ValidationError(
            message = "Bad request",
            fields = exception
                .constraintViolations
                .map { e ->
                    FieldError(e.propertyPath.drop(1).joinToString(separator = "."), e.message)
                }
        )

        return HttpResponse.badRequest(validationErrors)
            .contentType(MediaType.APPLICATION_JSON_TYPE)
    }
}