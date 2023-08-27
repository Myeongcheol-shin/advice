package com.shino72.saying.di.module

import com.shino72.saying.BuildConfig
import com.shino72.saying.service.ApiService
import com.shino72.saying.service.PhotoService
import com.shino72.saying.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideFirstApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.adviceUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSecondApiService(): PhotoService {
        return Retrofit.Builder()
            .baseUrl(Constants.imageUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PhotoService::class.java)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    } else {
        OkHttpClient.Builder().build()
    }


}
