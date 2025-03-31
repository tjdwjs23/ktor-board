package board.ktor

import board.ktor.repository.BoardRepository
import board.ktor.service.BoardService
import com.codersee.plugins.configureSecurity
import com.codersee.repository.RefreshTokenRepository
import com.codersee.repository.UserRepository
import com.codersee.routing.configureRouting
import com.codersee.service.JwtService
import com.codersee.service.UserService
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


    val userRepository = UserRepository()
    val refreshTokenRepository = RefreshTokenRepository()
    val jwtService = JwtService(this, userRepository)
    val userService = UserService(userRepository, jwtService, refreshTokenRepository)

    val boardRepository = BoardRepository(database)
    val boardService = BoardService(boardRepository)

    configureSerialization()
    configureSecurity(jwtService)
    configureRouting(userService, boardService)
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

