package board.ktor.model

import org.ktorm.entity.Entity
import org.ktorm.schema.*
import java.time.LocalDateTime


interface User : Entity<User> {
    val id: Long
    var email: String
    var username: String
    var password: String
    var createdAt: LocalDateTime
    var updatedAt: LocalDateTime?
}

object Users : Table<User>("USER") {
    val id = long("ID").primaryKey().bindTo(User::id)
    val email = varchar("EMAIL").bindTo(User::email)
    val username = text("USERNAME").bindTo(User::username)
    val password = text("PASSWORD").bindTo(User::password)
    val createdAt = datetime("CREATED_AT").bindTo(User::createdAt)
    val updatedAt = datetime("UPDATED_AT").bindTo(User::updatedAt)
}

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val createdAt: String
)

data class AuthResponse(
    val token: String
)