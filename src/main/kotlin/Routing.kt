package board.ktor

import io.ktor.http.cio.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import org.slf4j.MDC
import java.time.LocalDateTime


fun Route.sampleRouter() {
    get("/") {
        call.respond(Sample("doubleu0714", 30, LocalDateTime.now()))
    }

    post("/") {
        val sample = call.receive<Sample>()
        call.respond(sample)
    }

}

data class Sample(
    val name: String,
    val age: Int,
    val createdAt: LocalDateTime,
)