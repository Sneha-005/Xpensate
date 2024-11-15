package com.example.xpensate.API.home

data class ExpensesByCategory(
    val category: String,
    val total: Double
)