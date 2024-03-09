package org.example.config

import model.table.Categories
import model.table.Categories.name
import model.table.Transactions
import org.example.model.table.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseConfig {
    private val DB_USER: String = System.getenv("DB_USER")
    private val DB_PASS: String = System.getenv("DB_PASS")
    private val DB_NAME: String = System.getenv("DB_NAME")

    private val INSTANCE_HOST: String = System.getenv("INSTANCE_HOST")
    private val DB_PORT: String = System.getenv("DB_PORT")

    fun init() {

        val url = "jdbc:mysql://${INSTANCE_HOST}:${DB_PORT}/${DB_NAME}" // Adjust with your database name
        val driver = "com.mysql.cj.jdbc.Driver"
        val user = DB_USER // Your MySQL username
        val password = DB_PASS // Your MySQL password

        // Connect to the database
        Database.connect(url = url, driver = driver, user = user, password = password)

        transaction {
            // Create the tables if they don't exist
            SchemaUtils.create(Users, Transactions, Categories)

            // Pre-populate the Categories table with some default categories
            val defaultCategories = listOf(
                "Food",
                "Transportation",
                "Utilities",
                "Entertainment",
                "Healthcare",
                "Clothing",
                "Education",
                "Groceries",
                "Rent",
                "Mortgage",
                "Savings",
                "Investments",
                "Gifts",
                "Donations",
                "Miscellaneous"
            )
            for (category in defaultCategories) {
                val result = Categories.select { name eq category }.singleOrNull()
                if (result == null) {
                    Categories.insertIgnore {
                        it[name] = category
                    }
                }
            }
        }
    }
}
