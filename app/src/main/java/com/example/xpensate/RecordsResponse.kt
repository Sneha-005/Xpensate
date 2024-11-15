package com.example.xpensate

data class RecordsResponse(
    val expenses: List<RecordsResponseItem>,
    val total_expense: Double
)