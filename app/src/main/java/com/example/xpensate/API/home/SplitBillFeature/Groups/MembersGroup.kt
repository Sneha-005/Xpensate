package com.example.xpensate.API.home.SplitBillFeature.Groups

import com.google.gson.annotations.SerializedName

data class MembersGroup(
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("groupowner") val groupOwner: String,
    val id: Int,
    val members: List<Member>,
    val name: String
)