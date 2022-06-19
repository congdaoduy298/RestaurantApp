package com.amantech.foodrunner.model
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemOrder(
    var price: Int=0,
    var count: Int=0,
    var name: String="",
    var url : String=""
) : Parcelable