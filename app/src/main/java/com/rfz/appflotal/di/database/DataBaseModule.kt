package com.rfz.appflotal.di.database

import android.content.Context
import androidx.room.Room
import com.rfz.appflotal.data.dao.AppHCDao
import com.rfz.appflotal.data.dao.CoordinatesDao
import com.rfz.appflotal.data.dao.DataframeDao
import com.rfz.appflotal.data.dao.SensorDataDao
import com.rfz.appflotal.data.database.AppHombreCamionDatabase
import com.rfz.appflotal.data.model.database.SensorDataEntity

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
    fun provideDataframeDao(hombreCamionDatabase: AppHombreCamionDatabase): DataframeDao {
        return hombreCamionDatabase.dataframeDao()
    }

    @Provides
    fun provideCoordinatesDao(hombreCamionDatabase: AppHombreCamionDatabase): CoordinatesDao {
        return hombreCamionDatabase.coordinatesDao()
    }

    @Provides
    fun provideSensorDataDao(hombreCamionDatabase: AppHombreCamionDatabase): SensorDataDao {
        return hombreCamionDatabase.sensorDataDao()
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