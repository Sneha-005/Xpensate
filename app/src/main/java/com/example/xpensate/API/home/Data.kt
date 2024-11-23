package com.example.xpensate.API.home

data class Data(
    val amount: String,
    val bill_participants: List<BillParticipant>,
    val billdate: String,
    val billname: String,
    val group: Int,
    val id: Int
)