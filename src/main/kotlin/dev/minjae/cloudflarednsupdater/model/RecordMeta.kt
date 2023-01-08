package dev.minjae.cloudflarednsupdater.model

import com.fasterxml.jackson.annotation.JsonProperty

data class RecordMeta(
    val auto_added: Boolean,
    val source: String,
    @JsonProperty(value = "managed_by_apps", required = false)
    val managed_by_apps: Boolean,
    @JsonProperty(value = "managed_by_argo_tunnel", required = false)
    val managed_by_argo_tunnel: Boolean
)
