package com.example.xpensate.Modals


data class RecordsResponse(
    val expenses: List<RecordsResponseItem>,
    val total_expense: Double
)