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

    // CallLogging & DoubleReceive 위한 의존성
    implementation("io.ktor:ktor-server-status-pages:$ktor_version")

    // JWT 인증을 위한 의존성
    implementation("io.ktor:ktor-server-auth:$ktor_version") // 기본 인증 모듈
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version") // JWT 인증 모듈

    // BCrypt
    implementation("at.favre.lib:bcrypt:0.10.2")

    // MySQL Connector and HikariCP
    implementation("mysql:mysql-connector-java:$mysql_version")
    implementation("com.zaxxer:HikariCP:$hikari_version")

    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}