package com.example.xpensate.API.home

data class IncomevsExpense(
    val success: Boolean,
    val total_expense: Int,
    val total_income: Int
)