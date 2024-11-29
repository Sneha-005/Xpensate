package com.example.xpensate.API.home.SplitBillFeature

data class BillParticipant(
    val amount: String,
    val paid: Boolean,
    val participant: Participant
)