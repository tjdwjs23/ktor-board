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
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // Ktorm ORM
    implementation("org.ktorm:ktorm-core:$ktorm_version")
    implementation("org.ktorm:ktorm-jackson:$ktorm_version")
    implementation("org.ktorm:ktorm-support-mysql:$ktorm_version")

    // content negotiation(with Jackson)을 위한 의존성
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-jackson-jvm")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // CallLogging & DoubleReceive 위한 의존성
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-double-receive-jvm")
    implementation("ch.qos.logback:logback-classic:1.5.15")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    // MySQL Connector and HikariCP
    implementation("mysql:mysql-connector-java:$mysql_version")
    implementation("com.zaxxer:HikariCP:$hikari_version")

    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}