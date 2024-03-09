package model.request

import java.time.LocalDateTime

data class TransactionRequest(
    val userId: Int,
    val categoryId: Int,
    val amount: Double,
    val description: String,
    val date: LocalDateTime
)