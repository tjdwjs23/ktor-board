package board.ktor

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import org.ktorm.database.Database

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val config = HikariDataSource(
        HikariConfig().apply {
            jdbcUrl = "jdbc:mysql://localhost:3306/template"
            driverClassName = "com.mysql.jdbc.Driver"
            username = "user"
            password = "user123"
        }
    )

    val dataSource = HikariDataSource(config)
    val database = Database.connect(dataSource)

    configureRouting(database)
    configureSerialization()
}


fun Application.configureSerialization() {
    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
        }
    }
}