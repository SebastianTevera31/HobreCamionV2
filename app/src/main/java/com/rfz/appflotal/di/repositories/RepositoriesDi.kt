package com.rfz.appflotal.di.repositories

import com.rfz.appflotal.data.repository.UserPreferencesRepository
import com.rfz.appflotal.data.repository.UserPreferencesRepositoryImpl
import com.rfz.appflotal.data.repository.assembly.AssemblyTireRepository
import com.rfz.appflotal.data.repository.assembly.AssemblyTireRepositoryImpl
import com.rfz.appflotal.data.repository.fcmessaging.AppUpdateMessageRepository
import com.rfz.appflotal.data.repository.fcmessaging.AppUpdateMessageRepositoryImpl
import com.rfz.appflotal.data.repository.repair.RepairRepository
import com.rfz.appflotal.data.repository.repair.RepairRepositoryImpl
import com.rfz.appflotal.data.repository.tire.TireRepository
import com.rfz.appflotal.data.repository.tire.TireRepositoryImpl
import com.rfz.appflotal.data.repository.vehicle.VehicleRepository
import com.rfz.appflotal.data.repository.vehicle.VehicleRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DiRepository {

    @Binds
    @Singleton
    abstract fun provideAssemblyTire(impl: AssemblyTireRepositoryImpl): AssemblyTireRepository

    @Binds
    @Singleton
    abstract fun bindVehicleRepository(impl: VehicleRepositoryImpl): VehicleRepository

    @Binds
    @Singleton
    abstract fun bindTireRepository(impl: TireRepositoryImpl): TireRepository

    @Binds
    @Singleton
    abstract fun bindRepairRepository(impl: RepairRepositoryImpl): RepairRepository

    @Binds
    @Singleton
    abstract fun bindRepositoryFscMessaging(impl: AppUpdateMessageRepositoryImpl): AppUpdateMessageRepository

    @Binds
    @Singleton
    abstract fun bindRepositoryUserPreferences(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository
}