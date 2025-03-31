package com.codersee.routing

import board.ktor.routing.boardRoute
import board.ktor.service.BoardService
import com.codersee.service.UserService
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
  userService: UserService,
  boardService: BoardService
) {
  routing {

    staticResources("/content", "mycontent")
    staticResources("/user", "user" )

    route("/api/auth") {
      authRoute(userService)
    }

    route("/api/user") {
      userRoute(userService)
    }

    route("/api/board") {
      boardRoute(boardService)
    }

  }
}