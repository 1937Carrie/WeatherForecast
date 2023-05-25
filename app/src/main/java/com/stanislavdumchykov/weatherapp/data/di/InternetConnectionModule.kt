package com.stanislavdumchykov.weatherapp.data.di

import com.stanislavdumchykov.weatherapp.data.repository.InternetConnectionImpl
import com.stanislavdumchykov.weatherapp.domain.repository.InternetConnection
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object InternetConnectionModule {

    @Provides
    fun provideWeatherInterpretationImpl(): InternetConnection {
        return InternetConnectionImpl()
    }

}