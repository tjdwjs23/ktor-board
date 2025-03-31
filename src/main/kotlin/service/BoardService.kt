package board.ktor.service

import board.ktor.repository.BoardRepository
import board.ktor.routing.request.BoardRequest
import board.ktor.routing.response.BoardResponse

class BoardService(
    private val boardRepository: BoardRepository
) {
    fun findAll(): List<BoardResponse>? = boardRepository.findAll()

    fun findById(id: Long): BoardResponse? = boardRepository.findById(id)

    fun create(boardRequest : BoardRequest): BoardResponse? = boardRepository.create(boardRequest)

    fun update(id: Long, boardRequest: BoardRequest): BoardResponse? = boardRepository.update(id, boardRequest)

    fun delete(id: Long): Boolean = boardRepository.delete(id)
}