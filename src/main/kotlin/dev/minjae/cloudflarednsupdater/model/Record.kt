package dev.minjae.cloudflarednsupdater.model

data class Record(
    val id: String,
    val type: String,
    val name: String,
    val content: String,
    val proxiable: Boolean,
    val proxied: Boolean,
    val comment: String?,
    val tags: List<String>,
    val ttl: Int,
    val locked: Boolean,
    val zone_id: String,
    val zone_name: String,
    val created_on: String,
    val modified_on: String,
    val meta: RecordMeta
)
