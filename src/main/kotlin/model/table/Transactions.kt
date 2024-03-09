package org.example.model.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Transactions : Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("userId").references(Users.id) // Ensure this matches your Users table's ID column
    val amount = decimal("amount", 10, 2)
    val description = varchar("description", 255)
    val date = datetime("date") // Using `java-time` datetime for newer Exposed versions
    override val primaryKey: PrimaryKey = PrimaryKey(id)
}
