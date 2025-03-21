val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val coroutines_version: String by project
val java_sdk_version: String by project
val mysql_version = "8.0.33"
val hikari_version = "6.2.1"
val ktorm_version = "4.1.1"

plugins {
    kotlin("jvm") version "2.1.20"
    id("io.ktor.plugin") version "3.1.1"
}

group = "board.ktor"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Framework
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-jackson:$ktor_version")
    implementation("io.ktor:ktor-server-config-yaml:$ktor_version")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // Ktorm ORM
    implementation("org.ktorm:ktorm-core:$ktorm_version")
    implementation("org.ktorm:ktorm-jackson:$ktorm_version")
    implementation("org.ktorm:ktorm-support-mysql:$ktorm_version")

    // MySQL Connector and HikariCP
    implementation("mysql:mysql-connector-java:$mysql_version")
    implementation("com.zaxxer:HikariCP:$hikari_version")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}