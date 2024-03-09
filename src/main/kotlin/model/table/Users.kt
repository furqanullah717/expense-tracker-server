package org.example.model.table

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 255)
    val passwordHash = varchar("passwordHash", 64)

    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(id)
}