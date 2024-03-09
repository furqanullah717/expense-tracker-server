package org.example.config

import org.example.model.table.Transactions
import org.example.model.table.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseConfig {
    private val DB_USER: String = System.getenv("DB_USER")
    private val DB_PASS: String = System.getenv("DB_PASS")
    private val DB_NAME: String = System.getenv("DB_NAME")

    private val INSTANCE_HOST: String = System.getenv("INSTANCE_HOST")
    private val DB_PORT: String = System.getenv("DB_PORT")

    fun init() {

        val connectionString = String.format("jdbc:mysql://%s:%s/%s", INSTANCE_HOST, DB_PORT, DB_NAME)
        Database.connect(connectionString, driver = "org.h2.Driver", user = DB_USER, password = DB_PASS)
        transaction {
            // Create the tables if they don't exist
            SchemaUtils.create(Users, Transactions) // Include other tables as needed
        }
    }
}
