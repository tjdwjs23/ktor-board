package board.ktor.model

import org.ktorm.entity.Entity
import org.ktorm.schema.*
import java.time.LocalDateTime

interface Token : Entity<Token> {
    val id: Long
    var accessToken: String
    var refreshToken: String
    var user: Long
    var accessExpiresAt: LocalDateTime
    var refreshExpiresAt: LocalDateTime
    var revoked: Boolean
    var createdAt: LocalDateTime
}

object Tokens : Table<Token>("TOKEN") {
    val id = long("ID").primaryKey().bindTo(Token::id)
    val accessToken = varchar("ACCESS_TOKEN").bindTo(Token::accessToken)
    val refreshToken = varchar("REFRESH_TOKEN").bindTo(Token::refreshToken)
    val user = long("USER_ID").bindTo(Token::user)
    val accessExpiresAt = datetime("ACCESS_EXPIRES_AT").bindTo(Token::accessExpiresAt)
    val refreshExpiresAt = datetime("REFRESH_EXPIRES_AT").bindTo(Token::refreshExpiresAt)
    val revoked = boolean("REVOKED").bindTo(Token::revoked)
    val createdAt = datetime("CREATED_AT").bindTo(Token::createdAt)
}

data class RefreshTokenRequest(
    val refreshToken: String
)

data class TokenRecord(
    val id: Long,
    val user : Long,
    val accessToken: String,
    val refreshToken: String,
    val accessExpiresAt: LocalDateTime,
    val refreshExpiresAt: LocalDateTime,
    val revoked: Boolean,
    val createdAt: LocalDateTime
)