package dev.minjae.cloudflarednsupdater.model

data class ResultInfo(
    val page: Int,
    val per_page: Int,
    val count: Int,
    val total_count: Int,
    val total_pages: Int
)
