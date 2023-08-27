package com.shino72.saying.utils.data

import com.google.gson.annotations.SerializedName

data class PhotoResponse (
    @SerializedName("photos") val photos : List<Photo>
    )

data class Photo (
    @SerializedName("photographer") val photographer : String,
    @SerializedName("photographer_url") val photographer_url : String,
    @SerializedName("src") val src : Src
        )

data class Src(
    @SerializedName("large") val large : String
)