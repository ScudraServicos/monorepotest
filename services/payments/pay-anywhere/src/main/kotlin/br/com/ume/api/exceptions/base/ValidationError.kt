package br.com.ume.api.exceptions.base

data class ValidationError(
    val message: String,
    val fields: List<FieldError>
)