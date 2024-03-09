package routes

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import model.request.CategoryRequest
import model.resposne.ApiResponse
import model.resposne.CategoryResponse
import model.table.Categories
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.categoryRoutes() {
    route("/categories") {
        // Create a new category
        post("/") {
            val categoryRequest = call.receive<CategoryRequest>()
            val categoryId = transaction {
                Categories.insert {
                    it[name] = categoryRequest.name
                } get Categories.id
            }
            call.respond(
                HttpStatusCode.Created, ApiResponse<Any>(
                    success = true,
                    data = "Category created with ID: $categoryId"
                )
            )
        }

        // Get all categories
        get("/") {
            val categories = transaction {
                Categories.selectAll().map { CategoryResponse(it[Categories.id], it[Categories.name]) }
            }
            call.respond(ApiResponse<Any>(
                success = true,
                data = categories
            ))
        }

        // Get a single category by ID
        get("/{id}") {
            val categoryId = call.parameters["id"]?.toIntOrNull()
            if (categoryId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid category ID")
                return@get
            }
            val category = transaction {
                Categories.select { Categories.id eq categoryId }
                    .map { CategoryResponse(it[Categories.id], it[Categories.name]) }
                    .singleOrNull()
            }
            if (category != null) {
                call.respond(ApiResponse<Any>(
                    success = true,
                    data = category
                ))
            } else {
                call.respond(HttpStatusCode.NotFound, ApiResponse<Any>(
                    success = false,
                    message = "Category not found"
                ))
            }
        }

        // Update a category
        put("/{id}") {
            val categoryId = call.parameters["id"]?.toIntOrNull()
            val categoryRequest = call.receive<CategoryRequest>()
            if (categoryId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Any>(
                    success = true,
                    errors = listOf("Invalid Id")
                ))
                return@put
            }
            transaction {
                Categories.update({ Categories.id eq categoryId }) {
                    it[name] = categoryRequest.name
                }
            }
            call.respond(HttpStatusCode.OK, ApiResponse<Any>(
                success = true,
                data = "Success"
            ))
        }

        // Delete a category
        delete("/{id}") {
            val categoryId = call.parameters["id"]?.toIntOrNull()
            if (categoryId == null) {
                call.respond(HttpStatusCode.BadRequest, ApiResponse<Any>(
                    success = false,
                    message = "Invalid Id"
                ))
                return@delete
            }
            transaction {
                Categories.deleteWhere { Categories.id eq categoryId }
            }
            call.respond(HttpStatusCode.OK, ApiResponse<Any>(
                success = true,
                data = "Deleted"
            ))
        }
    }
}