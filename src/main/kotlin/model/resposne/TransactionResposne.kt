package model.resposne

import java.time.LocalDateTime

data class TransactionResponse(
    val id: Int,
    val userId: Int,
    val categoryId: Int,
    val amount: Double,
    val description: String,
    val date: LocalDateTime
)