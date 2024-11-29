package com.example.xpensate.API.TripTracker.CreateGroupResponse

data class Group(
    val created_at: String,
    val id: Int,
    val invitecode: String,
    val members: List<Member>,
    val name: String
)