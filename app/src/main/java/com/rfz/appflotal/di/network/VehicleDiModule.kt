package com.rfz.appflotal.di.network

import com.rfz.appflotal.data.network.client.alerts.AlertsService
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

@Module
@InstallIn(SingletonComponent::class)
object AlertDiModule {
    @Singleton
    @Provides
    fun provideAlertService(retrofit: Retrofit): AlertsService {
        return retrofit.create(AlertsService::class.java)
    }
}