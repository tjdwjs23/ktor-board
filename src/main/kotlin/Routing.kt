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
        staticResources("/content", "mycontent")

        // 게시글 등록
        post("/posts") {
            handlePostCreation(database, call)
        }

        // 게시글 목록 조회
        get("/posts") {
            handleGetPosts(database, call)
        }

        // 상세조회
        get("/posts/{id}") {
            handleGetPost(database, call)
        }

        // 게시글 수정
        put("/posts/{id}") {
            handlePostUpdate(database, call)
        }

        // 게시글 삭제
        delete("/posts/{id}") {
            handlePostDeletion(database, call)
        }
    }
}

private suspend fun handlePostCreation(database: Database, call: ApplicationCall) {
    val (title, content) = call.receiveParameters().let {
        it["title"] to it["content"]
    }.takeIf { (title, content) -> title != null && content != null }
        ?: return call.respondBadRequest()

    database.insert(Boards) {
        set(it.title, title)
        set(it.content, content)
        set(it.createdAt, LocalDateTime.now())
    }

    call.respondRedirect("/content/board.html")
}

private suspend fun handleGetPosts(database: Database, call: ApplicationCall) {
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

private suspend fun handleGetPost(database: Database, call: ApplicationCall) {
    val postId = call.parameters["id"]?.toLongOrNull() ?: return call.respondBadRequest()

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

private suspend fun handlePostUpdate(database: Database, call: ApplicationCall) {
    val postId = call.parameters["id"]?.toLongOrNull() ?: return call.respondBadRequest()
    val (title, content) = call.receiveParameters().let {
        it["title"] to it["content"]
    }.takeIf { (title, content) -> title != null && content != null }
        ?: return call.respondBadRequest()

    val updated = database.update(Boards) {
        set(it.title, title)
        set(it.content, content)
        set(it.updatedAt, LocalDateTime.now())
        where { it.id eq postId }
    }

    if (updated > 0) {
        call.respond(HttpStatusCode.NoContent)
    } else {
        call.respond(HttpStatusCode.NotFound)
    }
}

private suspend fun handlePostDeletion(database: Database, call: ApplicationCall) {
    val postId = call.parameters["id"]?.toLongOrNull() ?: return call.respondBadRequest()

    val deleted = database.delete(Boards) {
        it.id eq postId
    }

    if (deleted > 0) {
        call.respond(HttpStatusCode.NoContent)
    } else {
        call.respond(HttpStatusCode.NotFound)
    }
}

suspend fun ApplicationCall.respondBadRequest() {
    respond(TextContent("Invalid request", ContentType.Text.Plain, HttpStatusCode.BadRequest))
}
