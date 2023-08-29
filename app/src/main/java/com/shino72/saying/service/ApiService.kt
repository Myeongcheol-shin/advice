package com.shino72.saying.service

import com.shino72.saying.utils.data.PhotoResponse
import com.shino72.saying.utils.data.SlipResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {
    @GET("advice")
    suspend fun getAdvice () : Response<SlipResponse>
}

interface PhotoService {
    @GET("search")
    suspend fun getPhoto (@Header("Authorization") Authorization : String , @Query("query") query: String ,@Query("orientation") orientation : String, @Query("color") color : String, @Query("page") page: String, @Query("per_page") per_page : String) : Response<PhotoResponse>
}