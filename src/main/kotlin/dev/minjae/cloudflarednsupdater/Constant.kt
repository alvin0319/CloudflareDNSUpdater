package dev.minjae.cloudflarednsupdater

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.brotli.BrotliInterceptor
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.nio.file.Paths

object Constant {

    val appDataDir: Path = if (System.getProperty("workDir", "").isNotEmpty()) {
        Paths.get(System.getProperty("workDir"))
    } else {
        if (System.getProperty("os.name").contains("windows", ignoreCase = true)) {
            Paths.get(System.getenv("APPDATA")).resolve("CloudflareDNSUpdater")
        } else {
            Paths.get(System.getProperty("user.home")).resolve(".config").resolve("CloudflareDNSUpdater")
        }
    }.apply {
        if (!toFile().canRead() || !toFile().canWrite()) {
            throw InvalidPathException(this.toString(), "Cannot read or write to the directory")
        }
        if (!toFile().exists()) {
            toFile().mkdirs()
        }
    }

    val jacksonMapper: ObjectMapper = ObjectMapper().registerKotlinModule()

    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .followRedirects(true)
        .followSslRedirects(true)
        .addInterceptor(BrotliInterceptor)
        .cache(Cache(appDataDir.resolve("cache").toFile(), 1024 * 1024 * 10))
        .build()

    var X_AUTH_KEY = ""
    var X_AUTH_EMAIL = ""

    const val BASE_ENDPOINT = "https://api.cloudflare.com/client/v4/"
    const val CREATE_DNS_RECORD = "zones/{zoneId}/dns_records" // POST
    const val UPDATE_DNS_RECORD = "zones/{zoneId}/dns_records/{recordId}" // PUT
    const val GET_DNS_RECORD_DETAILS = "zones/{zoneId}/dns_records/{recordId}" // GET
    const val LIST_DNS_RECORDS = "zones/{zoneId}/dns_records" // GET

    fun baseRequest(endpoint: String, vararg params: Pair<String, String>): Request.Builder {
        @Suppress("NAME_SHADOWING")
        var endpoint = endpoint
        for ((key, value) in params) {
            endpoint = endpoint.replace("{$key}", value)
        }
        return Request.Builder()
            .url(BASE_ENDPOINT + endpoint.format())
            .header("Authorization", "Bearer $X_AUTH_KEY")
            .header("X-Auth-Email", X_AUTH_EMAIL)
    }
}
