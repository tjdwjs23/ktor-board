package board.ktor

import at.favre.lib.crypto.bcrypt.BCrypt
import board.ktor.model.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
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
        staticResources("/user", "user" )

        // 게시글 등록 예시
        authenticate("jwt") {
            post("/posts") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                // userId를 이용한 게시글 생성 로직
                handlePostCreation(database, call)
            }
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

        // 회원가입
        post("/register") {
            handleRegistration(database, call)
        }

        // 로그인
        post("/login") {
            handleLogin(database, call)
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

private suspend fun handleRegistration(database: Database, call: ApplicationCall) {
    val parameters = call.receiveParameters()
    val request = RegisterRequest(
        username = parameters["username"] ?: "",
        password = parameters["password"] ?: "",
        email = parameters["email"] ?: ""
    )

    val existingUser = database.from(Users)
        .select()
        .where { Users.username eq request.username }
        .map { row ->
            UserResponse(
                id = row[Users.id]!!,
                username = row[Users.username]!!,
                email = row[Users.email]!!,
                createdAt = row[Users.createdAt]!!.toString()
            )
        }
        .firstOrNull()

    println("existingUser: $existingUser")

    if (existingUser != null) {
        call.respond(HttpStatusCode.Conflict, "Username already exists")
        return
    }

    val hashedPassword = BCrypt.withDefaults().hashToString(12, request.password.toCharArray())

    println("hashedPassword: $hashedPassword")

    database.insert(Users) {
        set(it.username, request.username)
        set(it.password, hashedPassword)
        set(it.email, request.email)
        set(it.createdAt, LocalDateTime.now())
    }

    call.respond(HttpStatusCode.Created, UserResponse(
        id = 0,
        username = request.username,
        email = request.email,
        createdAt = LocalDateTime.now().toString()
    ))
}

private suspend fun handleLogin(database: Database, call: ApplicationCall) {
    val parameters = call.receiveParameters()
    val request = RegisterRequest(
        username = parameters["username"] ?: "",
        password = parameters["password"] ?: "",
        email = parameters["email"] ?: ""
    )

    val user = database.from(Users)
        .select()
        .where { Users.username eq request.username }
        .map { row ->
            Pair(
                UserResponse(
                    id = row[Users.id]!!,
                    username = row[Users.username]!!,
                    email = row[Users.email]!!,
                    createdAt = row[Users.createdAt]!!.toString()
                ),
                row[Users.password]!!
            )
        }
        .firstOrNull()

    if (user == null || !BCrypt.verifyer().verify(request.password.toCharArray(), user.second).verified) {
        call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
        return
    }

    val token = JWT.create()
        .withAudience("my_audience")
        .withIssuer("my_issuer")
        .withClaim("userId", user.first.id)
        .withClaim("username", user.first.username)
        .sign(Algorithm.HMAC256("your_secret_key"))

    call.respond(AuthResponse(token))
}

suspend fun ApplicationCall.respondBadRequest() {
    respond(TextContent("Invalid request", ContentType.Text.Plain, HttpStatusCode.BadRequest))
}
