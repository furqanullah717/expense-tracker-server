package model.table

import org.example.model.table.Users
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Transactions : Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("userId").references(Users.id) // Ensure this matches your Users table's ID column
    val amount = decimal("amount", 10, 2)
    val categoryId = integer("categoryId").references(Categories.id, onDelete = ReferenceOption.CASCADE)
    val description = varchar("description", 255)
    val date = datetime("date") // Using `java-time` datetime for newer Exposed versions
    override val primaryKey: PrimaryKey = PrimaryKey(id)
}
