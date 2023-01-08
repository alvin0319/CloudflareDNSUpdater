package dev.minjae.cloudflarednsupdater

import com.fasterxml.jackson.module.kotlin.readValue
import dev.minjae.cloudflarednsupdater.config.Config
import dev.minjae.cloudflarednsupdater.model.CreatedRecord
import dev.minjae.cloudflarednsupdater.model.Record
import dev.minjae.cloudflarednsupdater.model.Result
import dev.minjae.cloudflarednsupdater.model.Results
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Updater(private val config: Config) {

    private val threadPool = Executors.newSingleThreadScheduledExecutor()

    private var lastIP = ""

    private val logger = LoggerFactory.getLogger("Updater")

    init {
        Constant.X_AUTH_EMAIL = config.email
        Constant.X_AUTH_KEY = config.key
        threadPool.scheduleAtFixedRate(::check, 0, 5, TimeUnit.MINUTES)
        Runtime.getRuntime().addShutdownHook(Thread(::shutdown))
    }

    private fun shutdown() {
        threadPool.shutdown()
    }

    private fun check() {
        logger.info("Checking IP...")
        val request = Request.Builder()
            .url("https://checkip.amazonaws.com")
            .get()
            .build()

        val response = Constant.okHttpClient.newCall(request).execute().use {
            it.body.string()
        }
        var needChange = lastIP != response.trim()
        if (lastIP.isEmpty()) {
            lastIP = response.trim()
            needChange = true
        }
        if (needChange) {
            logger.info("Attempting to change DNS record for ${config.domainName}...")

            logger.info("Getting record information from Cloudflare...")
            val dnsRequest = Constant.baseRequest(
                "${Constant.LIST_DNS_RECORDS}?type=A&name=${config.domainName}",
                Pair("zoneId", config.zoneId)
            )
                .get()
                .build()

            Constant.okHttpClient.newCall(dnsRequest).execute().use {
                try {
                    val result: Results<Record> = Constant.jacksonMapper.readValue(it.body.string())
                    if (!result.success) {
                        logger.error("Failed to get record information from Cloudflare: ${result.errors.joinToString(", ")}")
                        return
                    }
                    if (result.results.size != 1) {
                        if (result.results.isEmpty()) {
                            logger.info("No records found for ${config.domainName}, creating new one...")
                            val createRequest = Constant.baseRequest(
                                Constant.CREATE_DNS_RECORD,
                                Pair("zoneId", config.zoneId)
                            )
                                .post(
                                    Constant.jacksonMapper.writeValueAsString(
                                        mapOf(
                                            "type" to "A",
                                            "name" to config.domainName,
                                            "content" to response.trim(),
                                            "ttl" to 1,
                                            "proxied" to false
                                        )
                                    ).toRequestBody()
                                )
                                .build()

                            @Suppress("NAME_SHADOWING")

                            Constant.okHttpClient.newCall(createRequest).execute().use {
                                val result: Result<CreatedRecord> = Constant.jacksonMapper.readValue(it.body.string())
                                if (result.success) {
                                    logger.info("Successfully created new record for ${config.domainName}")
                                } else {
                                    logger.error(
                                        "Failed to create new record for ${config.domainName}: ${
                                        result.errors.joinToString(
                                            ", "
                                        )
                                        }"
                                    )
                                }
                            }
                            return
                        }
                        logger.error("Expected exactly 1 record, but got ${result.results.size}")
                        return
                    }
                    val record = result.results[0]
                    logger.info("Record ID: ${record.id}")

                    if (record.content == response.trim()) {
                        logger.info("IP is already up to date, skipping...")
                        return
                    }

                    @Suppress("NAME_SHADOWING")
                    val request = Constant.baseRequest(
                        Constant.UPDATE_DNS_RECORD,
                        Pair("recordId", record.id),
                        Pair("zoneId", config.zoneId)
                    )
                        .put(
                            Constant.jacksonMapper.writeValueAsString(
                                mapOf(
                                    "type" to "A",
                                    "name" to config.domainName,
                                    "content" to response.trim()
                                )
                            ).toRequestBody()
                        )
                        .build()
                    Constant.okHttpClient.newCall(request).execute().use {
                        if (it.isSuccessful) {
                            logger.info("Successfully changed DNS record for ${config.domainName}")
                            lastIP = response.trim()
                        } else {
                            logger.error("Failed to change DNS record for ${config.domainName}")
                            logger.error(it.message)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
