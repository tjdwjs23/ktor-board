package board.ktor

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.slf4j.event.Level
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    contentNegotiation()
    callLogging()
    routing {
        sampleRouter()
    }
}


fun Application.contentNegotiation() {
    val javaTimeModule = JavaTimeModule().apply {
        addSerializer(LocalDateTime::class.java, object : JsonSerializer<LocalDateTime>() {
            override fun serialize(value: LocalDateTime, gen: JsonGenerator, serializer: SerializerProvider) {
                gen.writeString(
                    value.truncatedTo(ChronoUnit.SECONDS).atZone(ZoneId.systemDefault()).format(
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME
                    )
                )
            }
        })
        addDeserializer(LocalDateTime::class.java, object : JsonDeserializer<LocalDateTime>() {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): LocalDateTime {
                return LocalDateTime.parse(parser.valueAsString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            }
        })
    }

    install(ContentNegotiation) {
        jackson {
            registerModule(javaTimeModule)
            propertyNamingStrategy = PropertyNamingStrategies.SnakeCaseStrategy()
        }
    }
}

fun Application.callLogging() {
    install(DoubleReceive)
    install(CallLogging) {
        level = Level.INFO
        mdc("REQUEST_ID") { _ ->
            UUID.randomUUID().toString().substring(0, 7)
        }
        format { call ->
            val uri = call.request.uri
            val method = call.request.httpMethod.value
            val queryParams = call.request.queryParameters.toString()

            "uri=$uri, method=$method, queryParams=$queryParams"
        }
    }
}