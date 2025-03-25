package board.ktor

import board.ktor.model.BoardDto
import board.ktor.model.Boards
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.time.LocalDateTime


fun Application.configureRouting(database: Database) {
    routing {
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

        get("/posts") {
            val posts = database.from(Boards)
                .select()
                .map { row ->
                    BoardDto(
                        id = row[Boards.id]!!,
                        title = row[Boards.title]!!,
                        content = row[Boards.content]!!,
                        createdAt = row[Boards.createdAt]!!.toString(),
                        updatedAt = row[Boards.updatedAt]?.toString()
                    )
                }
            call.respond(posts)
        }

        get("/posts/{id}") {
            val postId = call.parameters["id"]?.toLongOrNull() ?: return@get call.respondBadRequest()

            val post = database.from(Boards)
                .select()
                .where { Boards.id eq postId }
                .map { row ->
                    BoardDto(
                        id = row[Boards.id]!!,
                        title = row[Boards.title]!!,
                        content = row[Boards.content]!!,
                        createdAt = row[Boards.createdAt]!!.toString(),
                        updatedAt = row[Boards.updatedAt]?.toString()
                    )
                }
                .singleOrNull()

            if (post != null) {
                call.respond(post)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

    }
}

suspend fun ApplicationCall.respondBadRequest() {
    respond(TextContent("Invalid request", ContentType.Text.Plain, HttpStatusCode.BadRequest))
}
