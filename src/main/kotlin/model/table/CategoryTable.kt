package model.table

import org.jetbrains.exposed.sql.Table

object Categories : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    override val primaryKey: PrimaryKey = PrimaryKey(id)
}