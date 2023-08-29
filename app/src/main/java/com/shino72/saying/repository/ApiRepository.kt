package com.shino72.saying.repository


import com.shino72.saying.BuildConfig
import com.shino72.saying.service.ApiService
import com.shino72.saying.service.PhotoService
import com.shino72.saying.utils.data.PhotoResponse
import com.shino72.saying.utils.data.SlipResponse
import retrofit2.Response
import javax.inject.Inject

class ApiRepository @Inject constructor(private val apiService: ApiService, private val photoService: PhotoService) {
    suspend fun getAdvice(): Response<SlipResponse> {
        return apiService.getAdvice()
    }

    suspend fun getImage(page: String): Response<PhotoResponse> {
        return photoService.getPhoto(BuildConfig.APIKEY, "nature", "square", "gray",page, "5")
    }

}



