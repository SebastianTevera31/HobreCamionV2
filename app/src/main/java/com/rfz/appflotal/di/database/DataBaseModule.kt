package com.rfz.appflotal.di.database

import android.content.Context
import androidx.room.Room
import com.rfz.appflotal.data.dao.AppHCDao
import com.rfz.appflotal.data.dao.SensorDao
import com.rfz.appflotal.data.database.AppHombreCamionDatabase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {
    @Provides
    fun provideHCDao(hombreCamionDatabase: AppHombreCamionDatabase): AppHCDao {
        return hombreCamionDatabase.hcSoftDao()
    }

    @Provides
    fun provideSensorDao(hombreCamionDatabase: AppHombreCamionDatabase): SensorDao {
        return hombreCamionDatabase.sensorDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppHombreCamionDatabase {
        return Room.databaseBuilder(
            appContext,
            AppHombreCamionDatabase::class.java,
            "AppFlotalDatabase"
        ).fallbackToDestructiveMigration(true).build()
    }
}