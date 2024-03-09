package routes

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import model.request.TransactionRequest
import model.resposne.ApiResponse
import model.resposne.TransactionResponse
import model.table.Categories
import model.table.Transactions
import org.example.model.table.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

fun Route.createTransaction() {
    post("/transactions") {
        val transactionRequest = call.receive<TransactionRequest>()
        val transactionId = transaction {
            Transactions.insert {
                it[userId] = transactionRequest.userId
                it[categoryId] = transactionRequest.categoryId
                it[amount] = transactionRequest.amount.toBigDecimal()
                it[description] = transactionRequest.description
                it[date] = transactionRequest.date
            } get Transactions.id
        }
        call.respond(
            HttpStatusCode.Created, ApiResponse<Any>(
                success = true,
                data = "Created Transaction"
            )
        )
    }
}

fun Route.getTransactionsByUser() {
    get("/transactions/{userId}") {
        val userId = call.parameters["userId"]?.toIntOrNull()
        val startDate = call.request.queryParameters["startDate"]
        val endDate = call.request.queryParameters["endDate"]
        if (userId == null) {
            call.respond(
                HttpStatusCode.BadRequest, ApiResponse<Any>(
                    success = false,
                    message = "Invalid User ID"
                )
            )
            return@get
        }
        val start = startDate?.let { LocalDate.parse(it) } ?: LocalDate.MIN
        val end = endDate?.let { LocalDate.parse(it) } ?: LocalDate.MAX
        val transactions = transaction {
            Transactions
                .join(Users, JoinType.INNER, additionalConstraint = { Transactions.userId eq Users.id })
                .join(Categories, JoinType.INNER, additionalConstraint = { Transactions.categoryId eq Categories.id })
                .select {
                    (Transactions.userId eq userId) and
                            (Transactions.date greaterEq start.atStartOfDay()) and
                            (Transactions.date lessEq end.plusDays(1)
                                .atStartOfDay()) // plusDays(1) to include the end date fully
                }

                .map {
                    TransactionResponse(
                        id = it[Transactions.id],
                        userId = it[Transactions.userId],
                        amount = it[Transactions.amount].toDouble(),
                        categoryId = it[Transactions.categoryId],
                        description = it[Transactions.description],
                        date = it[Transactions.date]
                    )
                }
        }
        call.respond(
            ApiResponse<Any>(
                success = true,
                data = transactions
            )
        )
    }
}

fun Route.updateTransaction() {
    put("/transactions/{id}") {
        val transactionId = call.parameters["id"]?.toIntOrNull()
        val transactionDto = call.receive<TransactionRequest>()
        if (transactionId == null) {
            call.respond(
                HttpStatusCode.BadRequest, ApiResponse<Any>(
                    success = false,
                    message  = "Invalid transaction ID"
                )
            )
            return@put
        }

        transaction {
            Transactions.update({ Transactions.id eq transactionId }) {
                it[amount] = transactionDto.amount.toBigDecimal()
                it[description] = transactionDto.description
            }
        }
        call.respond(HttpStatusCode.OK, ApiResponse<Any>(
            success = true,
            data = "Success"
        ))
    }
}

fun Route.deleteTransaction() {
    delete("/transactions/{id}") {
        val transactionId = call.parameters["id"]?.toIntOrNull()
        if (transactionId == null) {
            call.respond(HttpStatusCode.BadRequest, ApiResponse<Any>(
                success = false,
                message ="Invalid User ID"
            ))
            return@delete
        }

        transaction {
            Transactions.deleteWhere { Transactions.id eq transactionId }
        }
        call.respond(HttpStatusCode.OK, ApiResponse<Any>(
            success = true,
            data = "Deleted"
        ))
    }
}