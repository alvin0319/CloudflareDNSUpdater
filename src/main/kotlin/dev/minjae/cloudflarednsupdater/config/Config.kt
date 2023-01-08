package dev.minjae.cloudflarednsupdater.config

data class Config(
    val zoneId: String,
    val email: String,
    val key: String,
    val domainName: String
) {
    fun isValid() = zoneId.isNotBlank() && email.isNotBlank() && key.isNotBlank()
}
