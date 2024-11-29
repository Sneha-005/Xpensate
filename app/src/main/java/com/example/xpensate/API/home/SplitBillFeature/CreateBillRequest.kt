package com.example.xpensate.API.home.SplitBillFeature

data class CreateBillRequest(
    val amount: String,
    val bill_participants: List<BillParticipantX>,
    val group: String
)