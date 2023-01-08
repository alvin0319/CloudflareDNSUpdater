package dev.minjae.cloudflarednsupdater.model

data class Result<T : Any>(
    val success: Boolean,
    val errors: List<String>,
    val messages: List<String>,
    val result: T
)
