package com.dicoding.intermediate.storyapp.di

import com.dicoding.intermediate.storyapp.data.remote.ApiConfig
import com.dicoding.intermediate.storyapp.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService  = ApiConfig.getApiService()

}