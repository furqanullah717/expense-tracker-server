package expense.tracker

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import model.resposne.ApiResponse
import org.example.config.DatabaseConfig

import routes.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main() {
    embeddedServer(Netty, port = System.getenv("PORT").toInt()) {
        install(Authentication) {
            jwt("auth-jwt") {
                realm = "ktor sample"
                verifier(makeJwtVerifier())
                validate { credential ->
                    if (credential.payload.getClaim("email").asString() != "") {
                        JWTPrincipal(credential.payload)
                    } else {
                        null
                    }
                }
            }
        }

        DatabaseConfig.init()
        install(StatusPages) {
            exception<Throwable> { cause ->
                call.respond(
                    HttpStatusCode.InternalServerError, ApiResponse<Any>(
                    success = false,
                    message = "An internal server error occurred",
                    errors = listOf(cause.localizedMessage ?: "Unknown error")
                )
                )
            }
        }
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
                registerTypeAdapter(
                    LocalDateTime::class.java,
                    JsonSerializer<LocalDateTime> { src, typeOfSrc, context ->
                        JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    })
                registerTypeAdapter(
                    LocalDateTime::class.java,
                    JsonDeserializer { json, type, jsonDeserializationContext ->
                        LocalDateTime.parse(json.asJsonPrimitive.asString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    })
            }
        }
        routing {
            register()
            signIn()
            authenticate("auth-jwt") {
                createTransaction()
                getTransactionsByUser()
                updateTransaction()
                deleteTransaction()
                categoryRoutes()
            }
        }
    }.start(wait = true)
}


fun makeJwtVerifier(): JWTVerifier {
    val secret = System.getenv("SECRET_KEY") ?: "defaultSecret" // Use a strong, unique key
    return JWT
        .require(Algorithm.HMAC256(secret))
        .build()
}