package board.ktor.repository

import board.ktor.model.Boards
import board.ktor.routing.request.BoardRequest
import board.ktor.routing.response.BoardResponse
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.time.LocalDateTime

// 리포지토리 계층
class BoardRepository(private val database: Database) {
    fun findAll(): List<BoardResponse>? = database
        .from(Boards)
        .select()
        .map { row ->
            BoardResponse(
                id = row[Boards.id] ?: throw IllegalStateException("ID cannot be null"),
                title = row[Boards.title] ?: "",
                content = row[Boards.content] ?: "",
                createdBy = row[Boards.createdBy]!!,
                createdAt = row[Boards.createdAt]!!.toString(),
                updatedAt = row[Boards.updatedAt]?.toString()
            )
        }

    fun findById(id: Long): BoardResponse? = database.from(Boards)
            .select()
            .where { Boards.id eq id }
            .map { row ->
                BoardResponse(
                    id = row[Boards.id]!!,
                    title = row[Boards.title]!!,
                    content = row[Boards.content]!!,
                    createdBy = row[Boards.createdBy]!!,
                    createdAt = row[Boards.createdAt]!!.toString(),
                    updatedAt = row[Boards.updatedAt]?.toString()
                )
            }
            .singleOrNull()

    fun create(boardRequest : BoardRequest): BoardResponse? {
        val id = database.insertAndGenerateKey(Boards) {
            set(it.title, boardRequest.title)
            set(it.content, boardRequest.content)
        } as Long
        return findById(id)
    }

    fun update(id: Long, boardRequest: BoardRequest): BoardResponse? {
        database.update(Boards) {
            set(it.title, boardRequest.title)
            set(it.content, boardRequest.content)
            set(it.updatedAt, LocalDateTime.now())
            where {
                it.id eq id
            }
        }
        return findById(id)
    }

    fun delete(id: Long): Boolean = database.delete(Boards) {
        it.id eq id
    } > 0
}