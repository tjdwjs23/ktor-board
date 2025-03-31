package board.ktor.routing.response

data class BoardResponse(
    val id: Long,
    val title: String,
    val content: String,
    val createdBy: Long,
    val createdAt: String,
    val updatedAt: String?
)
