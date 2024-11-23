package com.example.xpensate.Modals

data class RecordsResponseItem(
    val amount: String,
    val category: String,
    val date: String,
    val id: Int,
    val image: Any,
    val is_credit: Boolean,
    val note: String,
    val time: String
)