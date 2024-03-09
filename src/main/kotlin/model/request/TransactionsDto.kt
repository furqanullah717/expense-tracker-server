package org.example.model.request

import java.time.LocalDateTime

data class TransactionsDto(
    val id: Int?,
    val userId: Int, // Ensure this matches your Users table's ID column
    val amount: Double,
    val description: String,
    val date: LocalDateTime
)