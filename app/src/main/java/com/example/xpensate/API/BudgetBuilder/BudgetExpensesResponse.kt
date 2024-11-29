package com.example.xpensate.API.BudgetBuilder

data class BudgetExpensesResponse(
    val luxury_credit_total: Int,
    val luxury_debit_total: Int,
    val needs_credit_total: Int,
    val needs_debit_total: Int,
    val success: Boolean
)