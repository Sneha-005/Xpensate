package com.example.xpensate.API.TripTracker.CreateGroupResponse

data class Expense(
    val amount: String,
    val id: Int,
    val is_paid: Boolean,
    val paidby_email: String,
    val share: Double,
    val whatfor: String
)