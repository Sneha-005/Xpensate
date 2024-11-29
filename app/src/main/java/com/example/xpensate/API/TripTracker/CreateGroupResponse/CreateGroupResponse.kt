package com.example.xpensate.API.TripTracker.CreateGroupResponse

data class CreateGroupResponse(
    val expenses: List<Expense>,
    val group: Group
)