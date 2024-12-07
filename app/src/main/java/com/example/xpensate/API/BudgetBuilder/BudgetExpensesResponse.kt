package com.example.xpensate.API.BudgetBuilder

data class BudgetExpensesResponse(
    val luxury_credit_total: Double,
    val luxury_debit_total: Double,
    val needs_credit_total: Double,
    val needs_debit_total: Double,
    val success: Boolean,
    val monthly: Int
)