package board.ktor.routing

import board.ktor.routing.request.BoardRequest
import board.ktor.service.BoardService
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.boardRoute(boardService: BoardService) {
    get {
        try {
            val boards = boardService.findAll()
            if (boards.isNullOrEmpty()) {
                call.respondText("No boards found", status = HttpStatusCode.NotFound)
            } else {
                call.respond(boards)
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error fetching boards: ${e.message}")
        }
    }

    get("/{id}") {
        val id = call.parameters.longOrBadRequest("id")

        try {
            val board = boardService.findById(id)
            board?.let {
                call.respond(board)
            } ?: call.respond(HttpStatusCode.NotFound, "Board not found")

        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error fetching board: ${e.message}")
        }
    }

    post {
        val board = call.receive<BoardRequest>()
        try {
            val createdBoard = boardService.create(board)
            createdBoard?.let {
                call.respond(createdBoard)
            } ?: call.respond(HttpStatusCode.InternalServerError, "Error creating board")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error creating board: ${e.message}")
        }
    }

    put("/{id}") {
        val id = call.parameters.longOrBadRequest("id")

        val board = call.receive<BoardRequest>()
        try {
            val updatedBoard = boardService.update(id, board)
            updatedBoard?.let {
                call.respond(updatedBoard)
            } ?: call.respond(HttpStatusCode.InternalServerError, "Error updating board")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error updating board: ${e.message}")
        }
    }

    delete("/{id}") {
        val id = call.parameters.longOrBadRequest("id")

        try {
            val deleted = boardService.delete(id)
            deleted.let {
                call.respond(HttpStatusCode.OK, "Board deleted")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error deleting board: ${e.message}")
        }
    }
}

fun Parameters.longOrBadRequest(name: String): Long
    = get(name)?.toLongOrNull()
        ?: throw BadRequestException("Invalid $name parameter")
