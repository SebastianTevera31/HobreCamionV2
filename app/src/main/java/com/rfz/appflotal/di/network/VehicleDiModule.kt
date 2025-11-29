package com.rfz.appflotal.di.network

import com.rfz.appflotal.data.network.client.vehicle.VehicleService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VehicleDiModule {
    @Singleton
    @Provides
    fun provideVehicleService(retrofit: Retrofit): VehicleService {
        return retrofit.create(VehicleService::class.java)
    }
}