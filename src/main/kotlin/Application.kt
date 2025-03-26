package board.ktor

import board.ktor.model.TokenRecord
import board.ktor.model.Tokens
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.time.LocalDateTime

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val config = environment.config

    val audience = "my_audience"

    val dataSource = HikariDataSource(
        HikariConfig().apply {
            jdbcUrl = config.property("ktor.database.jdbcUrl").getString()
            driverClassName = config.property("ktor.database.driverClassName").getString()
            username = config.property("ktor.database.username").getString()
            password = config.property("ktor.database.password").getString()
        }
    )

    val database = Database.connect(dataSource)

    fun audienceMatches(
        credential: JWTCredential,
    ): Boolean =
        credential.payload.audience.contains(audience)

    install(Authentication) {
        jwt("jwt") {
            realm = "myrealm"
            verifier(
                JWT.require(Algorithm.HMAC256("your_secret_key"))
                    .withAudience("my_audience")
                    .withIssuer("my_issuer")
                    .build()
            )
            validate { credential ->
                if (audienceMatches(credential)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }

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
            call.respond(
                HttpStatusCode.InternalServerError,
                CommonErrorResponse(
                    errorCode = "INTERNAL_SERVER_ERROR",
                    errorMessage = "내부 오류입니다.",
                    causeMessage = cause.message
                )
            )
        }
        exception<NoSuchElementException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                CommonErrorResponse(
                    errorCode = "NO_SUCH_ELEMENT",
                    errorMessage = "조회 결과가 없습니다.",
                    causeMessage = cause.message
                )
            )
        }
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respond(
                HttpStatusCode.NotFound,
                CommonErrorResponse(
                    errorCode = "NOT_FOUND",
                    errorMessage = "요청한 자원을 찾을 수 없습니다.",
                    causeMessage = null,
                )
            )
        }

    }

}

data class CommonErrorResponse(
    val errorCode: String,
    val errorMessage: String,
    val causeMessage: String?,
)

