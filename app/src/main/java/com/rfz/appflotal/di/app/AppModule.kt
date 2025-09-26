package com.rfz.appflotal.di.app


import android.content.Context
import com.rfz.appflotal.data.model.login.response.AppFlotalMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppFlotalMapper(): AppFlotalMapper {
        return AppFlotalMapper()
    }
}