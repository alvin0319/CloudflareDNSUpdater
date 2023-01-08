import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.minjae.cloudflarednsupdater"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.squareup.okhttp3:okhttp-brotli:5.0.0-alpha.11")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.1")
    implementation("com.fasterxml.jackson.module:jackson-module-blackbird:2.14.1")
    implementation("ch.qos.logback:logback-classic:1.4.5")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_19.toString()
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        manifest {
            attributes(mapOf("Main-Class" to "dev.minjae.cloudflarednsupdater.MainKt"))
        }
    }
}
