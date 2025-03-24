package board.ktor

import board.ktor.model.Board
import board.ktor.model.BoardDto
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.database.Database
import org.ktorm.dsl.from
import org.ktorm.dsl.insert
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.long
import org.ktorm.schema.text
import java.time.LocalDateTime
import board.ktor.model.Boards


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

        // 게시글 목록 조회 API
        get("/posts") {
            val posts = database
                .from(Boards)
                .select()
                .map { row ->
                    BoardDto(
                        id = row[Boards.id]!!,
                        title = row[Boards.title]!!,
                        content = row[Boards.content]!!,
                        createdAt = row[Boards.createdAt]!!,
                        updatedAt = row[Boards.updatedAt]
                    )
                }
            call.respond(posts)
        }
    }
}

suspend fun ApplicationCall.respondBadRequest() = respond(
    TextContent("Invalid request", ContentType.Text.Plain, HttpStatusCode.BadRequest)
)