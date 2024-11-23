package com.example.xpensate.API.home

data class BillParticipant(
    val amount: String,
    val paid: Boolean,
    val participant: Participant
)