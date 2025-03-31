package board.ktor.routing.request

data class BoardRequest(
    val title: String,
    val content: String
)