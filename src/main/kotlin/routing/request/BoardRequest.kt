package board.ktor.routing.request

data class BoardRequest(
    val title: String,
    val content: String
){
    init {  // 유효성 검증
        require(title.isNotBlank()) { "Title cannot be blank" }
        require(content.length >= 10) { "Content too short" }
    }
}
