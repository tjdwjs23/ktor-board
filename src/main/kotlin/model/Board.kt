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
    val id = long("ID").primaryKey().bindTo { it.id }
    val title = text("TITLE").bindTo { it.title }
    val content = text("CONTENT").bindTo { it.content }
    val createdAt = datetime("CREATED_AT").bindTo { it.createdAt }
    val updatedAt = datetime("UPDATED_AT").bindTo { it.updatedAt }
}

data class BoardDto(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)