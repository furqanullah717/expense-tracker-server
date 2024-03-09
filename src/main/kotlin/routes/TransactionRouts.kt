package org.example.routes

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import org.example.model.request.TransactionsDto
import org.example.model.table.Transactions
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.LocalDateTime

fun Route.createTransaction() {
    post("/transactions") {
        val transactionDto = call.receive<TransactionsDto>()
        val insertedId = transaction {
            Transactions.insert {
                it[userId] = transactionDto.userId
                it[amount] = transactionDto.amount.toBigDecimal()
                it[description] = transactionDto.description
                it[date] = transactionDto.date
            } get Transactions.id
        }
        call.respond(HttpStatusCode.Created, insertedId)
    }
}

fun Route.getTransactionsByUser() {
    get("/transactions/{userId}") {
        val userId = call.parameters["userId"]?.toIntOrNull()
        val startDate = call.request.queryParameters["startDate"]
        val endDate = call.request.queryParameters["endDate"]
        if (userId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
            return@get
        }
        val start = startDate?.let { LocalDate.parse(it) } ?: LocalDate.MIN
        val end = endDate?.let { LocalDate.parse(it) } ?: LocalDate.MAX
        val transactions = transaction {
            Transactions.select {
                (Transactions.userId eq userId) and
                        (Transactions.date greaterEq start.atStartOfDay()) and
                        (Transactions.date lessEq end.plusDays(1)
                            .atStartOfDay()) // plusDays(1) to include the end date fully
            }
                .map {
                    TransactionsDto(
                        id = it[Transactions.id],
                        userId = it[Transactions.userId],
                        amount = it[Transactions.amount].toDouble(),
                        description = it[Transactions.description],
                        date = it[Transactions.date]
                    )
                }
        }
        call.respond(transactions)
    }
}

fun Route.updateTransaction() {
    put("/transactions/{id}") {
        val transactionId = call.parameters["id"]?.toIntOrNull()
        val transactionDto = call.receive<TransactionsDto>()
        if (transactionId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid transaction ID")
            return@put
        }

        transaction {
            Transactions.update({ Transactions.id eq transactionId }) {
                it[amount] = transactionDto.amount.toBigDecimal()
                it[description] = transactionDto.description
            }
        }
        call.respond(HttpStatusCode.OK, "Transaction updated")
    }
}

fun Route.deleteTransaction() {
    delete("/transactions/{id}") {
        val transactionId = call.parameters["id"]?.toIntOrNull()
        if (transactionId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid transaction ID")
            return@delete
        }

        transaction {
            Transactions.deleteWhere { Transactions.id eq transactionId }
        }
        call.respond(HttpStatusCode.OK, "Transaction deleted")
    }
}