package com.example.xpensate.API.home

data class IncomevsExpense(
    val success: Boolean,
    val total_expense: Double,
    val total_income: Double
)