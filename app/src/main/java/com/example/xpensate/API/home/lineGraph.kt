package com.example.xpensate.API.home

data class lineGraph(
    val expenses_by_day: List<ExpensesByDay>,
    val total_expenses: Double
)