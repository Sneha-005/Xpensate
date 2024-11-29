package com.example.xpensate.API.home.SplitBillFeature.SplitGroupShowDetails

data class Data(
    val bills: List<Bill>,
    val created_at: String,
    val groupowner: String,
    val id: Int,
    val members: List<Member>,
    val name: String
)