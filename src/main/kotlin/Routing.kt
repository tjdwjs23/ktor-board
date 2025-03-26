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
import java.util.*

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
                handlePostCreation(database, call, userId)
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

        // 토큰 갱신
        post("/refresh") {
            handleRefreshToken(database, call)
        }

        post("/logout") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.payload?.getClaim("userId")?.asLong()
            val accessToken = principal?.payload?.getClaim("accessToken")?.asString()

            if (userId != null && accessToken != null) {
                database.update(Tokens) {
                    set(it.revoked, true)
                    where {
                        (Tokens.user eq userId) and (Tokens.accessToken eq accessToken)
                    }
                }
                call.respond(HttpStatusCode.OK, "Logged out")
            } else {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}

private suspend fun handlePostCreation(database: Database, call: ApplicationCall, userId : Long?) {
    val request = call.receive<BoardRequest>()
    database.insert(Boards) {
        set(it.title, request.title)
        set(it.content, request.content)
        set(it.createdAt, LocalDateTime.now())
        set(it.createdBy, userId)
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
                createdBy = row[Boards.createdBy]!!,
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
                createdBy = row[Boards.createdBy]!!,
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

    if (existingUser != null) {
        call.respond(HttpStatusCode.Conflict, "Username already exists")
        return
    }

    val hashedPassword = BCrypt.withDefaults().hashToString(12, request.password.toCharArray())

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
    val request = call.receive<LoginRequest>()

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

    // Generate access token (15 minutes expiry)
    val accessToken = JWT.create()
        .withAudience("my_audience")
        .withIssuer("my_issuer")
        .withClaim("userId", user.first.id)
        .withClaim("username", user.first.username)
        .withClaim("email", user.first.email)
        .withExpiresAt(Date(System.currentTimeMillis() + 60_000 * 60))
        .sign(Algorithm.HMAC256("your_secret_key"))

    // Generate refresh token (7 days expiry)
    val refreshToken = JWT.create()
        .withAudience("my_audience")
        .withIssuer("my_issuer")
        .withClaim("userId", user.first.id)
        .withExpiresAt(Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
        .sign(Algorithm.HMAC256("your_secret_key"))

    // Save tokens to database
    database.insert(Tokens) {
        set(it.accessToken, accessToken)
        set(it.refreshToken, refreshToken)
        set(it.user, user.first.id)
        set(it.accessExpiresAt, LocalDateTime.now().plusMinutes(15))
        set(it.refreshExpiresAt, LocalDateTime.now().plusDays(7))
        set(it.revoked, false)
        set(it.createdAt, LocalDateTime.now())
    }
    call.respond(AuthResponse(accessToken, refreshToken))
}

private suspend fun handleRefreshToken(database: Database, call: ApplicationCall) {
    val request = call.receive<RefreshTokenRequest>()

    // Find valid refresh token
    val tokenRecord = database.from(Tokens)
        .select()
        .where {
            Tokens.refreshToken eq request.refreshToken and
                    (Tokens.revoked eq false) and
                    (Tokens.refreshExpiresAt greater LocalDateTime.now())
        }
        .map { row ->
            TokenRecord(
                id = row[Tokens.id]!!,
                user = row[Tokens.user]!!,
                accessToken = row[Tokens.accessToken]!!,
                refreshToken = row[Tokens.refreshToken]!!,
                accessExpiresAt = row[Tokens.accessExpiresAt]!!,
                refreshExpiresAt = row[Tokens.refreshExpiresAt]!!,
                revoked = row[Tokens.revoked]!!,
                createdAt = row[Tokens.createdAt]!!
            )
        }
        .singleOrNull()

    if (tokenRecord == null) {
        call.respond(HttpStatusCode.Unauthorized, "Invalid or expired refresh token")
        return
    }

    // Revoke the used refresh token
    database.update(Tokens) {
        set(it.revoked, true)
        where { it.id eq tokenRecord.id }
    }

    // Fetch user details
    val user = database.from(Users)
        .select()
        .where { Users.id eq tokenRecord.user }
        .map { row ->
            UserResponse(
                id = row[Users.id]!!,
                username = row[Users.username]!!,
                email = row[Users.email]!!,
                createdAt = row[Users.createdAt]!!.toString()
            )
        }
        .singleOrNull() ?: run {
        call.respond(HttpStatusCode.NotFound, "User not found")
        return
    }

    // Generate new tokens
    val newAccessToken = JWT.create()
        .withAudience("my_audience")
        .withIssuer("my_issuer")
        .withClaim("userId", user.id)
        .withClaim("username", user.username)
        .withClaim("email", user.email)
        .withExpiresAt(Date(System.currentTimeMillis() + 15 * 60 * 1000))
        .sign(Algorithm.HMAC256("your_secret_key"))

    val newRefreshToken = JWT.create()
        .withAudience("my_audience")
        .withIssuer("my_issuer")
        .withClaim("userId", user.id)
        .withExpiresAt(Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
        .sign(Algorithm.HMAC256("your_secret_key"))

    // Save new tokens
    database.insert(Tokens) {
        set(it.accessToken, newAccessToken)
        set(it.refreshToken, newRefreshToken)
        set(it.user, user.id)
        set(it.accessExpiresAt, LocalDateTime.now().plusMinutes(15))
        set(it.refreshExpiresAt, LocalDateTime.now().plusDays(7))
        set(it.revoked, false)
        set(it.createdAt, LocalDateTime.now())
    }

    call.respond(AuthResponse(newAccessToken, newRefreshToken))
}

suspend fun ApplicationCall.respondBadRequest() {
    respond(TextContent("Invalid request", ContentType.Text.Plain, HttpStatusCode.BadRequest))
}
