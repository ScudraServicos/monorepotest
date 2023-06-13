package br.com.ume.api.exceptions.base

data class FieldError(
    val field: String,
    val message: String
)
