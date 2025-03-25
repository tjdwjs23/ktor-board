package board.ktor

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.ktorm.database.Database

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}


fun Application.module() {
    val config = environment.config

    val dataSource = HikariDataSource(
        HikariConfig().apply {
            jdbcUrl = config.property("ktor.database.jdbcUrl").getString()
            driverClassName = config.property("ktor.database.driverClassName").getString()
            username = config.property("ktor.database.username").getString()
            password = config.property("ktor.database.password").getString()
        }
    )

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
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            if (cause is IllegalArgumentException) {
                call.respondText(text = "403: $cause", status = HttpStatusCode.Forbidden)
            } else {
                call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
            }
        }
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respondText("404: Page not found", status = HttpStatusCode.NotFound)
        }
    }

}
