package com.shino72.saying.utils

import com.bumptech.glide.load.engine.Resource


sealed class Status<T>(val data : T? = null ,val message: String? = null) {
    class Success<T>(data : T?) :Status<T>(data = data)
    class Error<T>(message: String) : Status<T>(message = message)
    class Loading<T>() : Status<T>()
}