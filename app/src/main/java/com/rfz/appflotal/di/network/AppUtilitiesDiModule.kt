package com.rfz.appflotal.di.network

import com.rfz.appflotal.data.network.client.apputilities.AppUtilitiesService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppUtilitiesDiModule {
    @Singleton
    @Provides
    fun provideAppUtilitiesService(retrofit: Retrofit): AppUtilitiesService {
        return retrofit.create(AppUtilitiesService::class.java)
    }
}