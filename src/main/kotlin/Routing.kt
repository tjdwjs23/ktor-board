package board.ktor

import board.ktor.model.Board
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.long
import org.ktorm.schema.text
import java.time.LocalDateTime


object Boards : Table<Board>("BOARD") {
    val id = long("ID").primaryKey().bindTo { it.id }
    val title = text("TITLE").bindTo { it.title }
    val content = text("CONTENT").bindTo { it.content }
    val createdAt = datetime("CREATED_AT").bindTo { it.createdAt }
    val updatedAt = datetime("UPDATED_AT").bindTo { it.updatedAt }
}

fun Application.configureRouting(database: Database) {
    routing {
        // 글 등록 처리
        post("/posts") {
            val formParameters = call.receiveParameters()
            val title = formParameters["title"] ?: return@post call.respondBadRequest()
            val content = formParameters["content"] ?: return@post call.respondBadRequest()

            database.insert(Boards) {
                set(it.title, title)
                set(it.content, content)
                set(it.createdAt, LocalDateTime.now())
            }

            call.respondRedirect("/content/board.html")
        }

        staticResources("/content", "mycontent")
    }
}

suspend fun ApplicationCall.respondBadRequest() = respond(
    TextContent("Invalid request", ContentType.Text.Plain, HttpStatusCode.BadRequest)
)