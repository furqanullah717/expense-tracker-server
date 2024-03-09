package org.example.config

import org.example.model.table.Transactions
import org.example.model.table.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseConfig {
    fun init() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        transaction {
            // Create the tables if they don't exist
            SchemaUtils.create(Users, Transactions) // Include other tables as needed
        }
    }
}
