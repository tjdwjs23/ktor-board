package board.ktor.model

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.long
import org.ktorm.schema.text
import java.time.LocalDateTime

interface Board : Entity<Board> {
    val id: Long
    var title: String
    var content: String
    var createdAt: LocalDateTime
    var updatedAt: LocalDateTime?
}

object Boards : Table<Board>("BOARD") {
    val id = long("ID").primaryKey().bindTo(Board::id)
    val title = text("TITLE").bindTo(Board::title)
    val content = text("CONTENT").bindTo(Board::content)
    val createdAt = datetime("CREATED_AT").bindTo(Board::createdAt)
    val updatedAt = datetime("UPDATED_AT").bindTo(Board::updatedAt)
}

data class BoardRequest(
    val title: String,
    val content: String
)

data class BoardDto(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: String,
    val updatedAt: String?
)
