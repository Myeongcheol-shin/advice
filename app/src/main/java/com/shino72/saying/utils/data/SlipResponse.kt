package com.shino72.saying.utils.data

import com.google.gson.annotations.SerializedName

data class SlipResponse(
    @SerializedName("slip") val slip : Slip
)
data class Slip (
    @SerializedName("id") val id : Int,
    @SerializedName("advice")  val advice : String = ""
)