package com.example.xpensate.API.home.SplitBillFeature.Groups

import android.os.Parcel
import android.os.Parcelable

data class OwnerGroup(
    val id: String,
    val name: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OwnerGroup> {
        override fun createFromParcel(parcel: Parcel): OwnerGroup {
            return OwnerGroup(parcel)
        }

        override fun newArray(size: Int): Array<OwnerGroup?> {
            return arrayOfNulls(size)
        }
    }
}