package dev.minjae.cloudflarednsupdater

import com.fasterxml.jackson.module.kotlin.readValue
import dev.minjae.cloudflarednsupdater.config.Config
import org.slf4j.LoggerFactory

fun main() {
    val logger = LoggerFactory.getLogger("Main")
    val config: Config = Constant.appDataDir.resolve("config.json").toFile().apply {
        if (!exists()) {
            createNewFile()
            writeBytes({}.javaClass.getResourceAsStream("/config.json")!!.readBytes())
        }
    }.inputStream().buffered().use(Constant.jacksonMapper::readValue)
    if (!config.isValid()) {
        logger.info("Please fill the all values in ${Constant.appDataDir.resolve("config.json")}")
        return
    }
    Updater(config)
}
