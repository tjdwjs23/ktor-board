package board.ktor.model

import org.ktorm.entity.Entity
import org.ktorm.schema.*
import java.time.LocalDateTime

interface Board : Entity<Board> {
    val id: Long
    var title: String
    var content: String
    var createdAt: LocalDateTime
    var updatedAt: LocalDateTime?
}
