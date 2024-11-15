package com.example.xpensate.API.home

data class CategoryChartResponse(
    val expenses_by_category: List<ExpensesByCategory>,
    val total_expenses: Double
)