package board.ktor.model

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.long
import org.ktorm.schema.text
import java.time.LocalDateTime


interface User : Entity<User> {
    companion object : Entity.Factory<User>() // Add this
    val id: Long
    var username: String
    var password: String
    var createdAt: LocalDateTime
    var updatedAt: LocalDateTime?
}

object Users : Table<User>("USER") {
    val id = long("ID").primaryKey().bindTo(User::id)
    val username = text("USERNAME").bindTo(User::username)
    val password = text("PASSWORD").bindTo(User::password)
    val createdAt = datetime("CREATED_AT").bindTo(User::createdAt)
    val updatedAt = datetime("UPDATED_AT").bindTo(User::updatedAt)
}

data class RegisterRequest(
    val username: String,
    val password: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class UserResponse(
    val id: Long,
    val username: String,
    val createdAt: String
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)