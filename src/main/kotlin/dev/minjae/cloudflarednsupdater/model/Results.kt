package dev.minjae.cloudflarednsupdater.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Results<T : Any>(
    val success: Boolean,
    val errors: List<String>,
    val messages: List<String>,
    @JsonProperty(value = "result")
    val results: List<T>,
    @JsonProperty(required = false)
    val result_info: ResultInfo
)
