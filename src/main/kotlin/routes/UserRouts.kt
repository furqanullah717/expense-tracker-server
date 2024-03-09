package org.example.routes

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.example.model.request.User
import org.example.model.table.Users
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Route.register() {
    post("/register") {
        val user = call.receive<org.example.model.request.User>()
        val hashedPassword = BCrypt.withDefaults().hashToString(12, user.password.toCharArray())
        transaction {
            Users.insert {
                it[email] = user.email
                it[passwordHash] = hashedPassword
            }
        }
        call.respond(HttpStatusCode.Created, "User registered successfully")
    }
}

fun Route.signIn() {
    post("/login") {
        val user = call.receive<User>()
        val userInDb = transaction {
            Users.select { Users.email eq user.email }.singleOrNull()
        }

        if (userInDb == null || !BCrypt.verifyer()
                .verify(user.password.toCharArray(), userInDb[Users.passwordHash]).verified
        ) {
            call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            return@post
        }

        val token = JWT.create()
            .withSubject("Authentication")
            .withIssuer("ktorApp")
            .withClaim("email", user.email)
            .withExpiresAt(Date(System.currentTimeMillis() + 31536000)) // 10 minutes for simplicity
            .sign(
                Algorithm.HMAC256(
                    System.getenv("SECRET_KEY") ?: "defaultSecret"
                )
            ) // Use a secure way to store and retrieve the secret

        call.respond(mapOf("token" to token))
    }
}