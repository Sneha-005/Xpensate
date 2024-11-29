package com.example.xpensate.API.home.SplitBillFeature.Groups

import com.google.gson.annotations.SerializedName

data class GroupsResponse(
    @SerializedName("members groups") val membersGroups: List<MembersGroup>,
    @SerializedName("owner groups") val ownerGroups: List<OwnerGroup>
)