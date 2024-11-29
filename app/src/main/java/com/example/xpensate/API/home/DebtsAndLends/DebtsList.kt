package com.example.xpensate.API.home.DebtsAndLends

data class DebtsList(
    val amount: String,
    val date: String,
    val description: String,
    val id: Int,
    var is_paid: Boolean,
    val lend: Boolean,
    val name: String,
    val time: String,
    val user: Int
)