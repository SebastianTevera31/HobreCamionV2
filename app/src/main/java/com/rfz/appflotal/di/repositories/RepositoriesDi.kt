package com.rfz.appflotal.di.repositories

import android.content.Context
import androidx.work.WorkManager
import com.rfz.appflotal.data.repository.assembly.AssemblyTireRepository
import com.rfz.appflotal.data.repository.assembly.AssemblyTireRepositoryImpl
import com.rfz.appflotal.data.repository.vehicle.VehicleRepository
import com.rfz.appflotal.data.repository.vehicle.VehicleRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AssemblyTireRepository {

    @Binds
    @Singleton
    abstract fun provideAssemblyTire(impl: AssemblyTireRepositoryImpl): AssemblyTireRepository

    @Binds
    @Singleton
    abstract fun bindVehicleRepository(impl: VehicleRepositoryImpl): VehicleRepository
}

@Module
@InstallIn(SingletonComponent::class)
object WorkManagerModule {
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext contex: Context): WorkManager {
        return WorkManager.getInstance(contex)
    }
}