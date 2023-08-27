package com.shino72.saying.utils

import com.shino72.saying.utils.data.PhotoResponse


data class ImageState(
    val error: String = "",
    val isLoading: Boolean = false,
    val data : PhotoResponse? = null
)