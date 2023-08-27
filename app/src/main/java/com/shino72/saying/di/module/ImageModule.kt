package com.shino72.saying.di.module

import android.app.Application
import android.content.Context
import com.shino72.saying.repository.ImageSaveRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ImageModule {

    @Provides
    fun provideContext(application: Application): Context {
        return application
    }

    @Provides
    fun provideImage(context : Context) : ImageSaveRepository {
        return ImageSaveRepository(context)
    }
}