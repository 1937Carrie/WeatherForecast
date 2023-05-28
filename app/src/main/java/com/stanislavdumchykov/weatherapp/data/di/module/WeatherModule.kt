package com.stanislavdumchykov.weatherapp.data.di.module

import com.stanislavdumchykov.weatherapp.data.repository.WeatherInterpretationCodeImpl
import com.stanislavdumchykov.weatherapp.domain.repository.WeatherInterpretation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object WeatherModule {

    @Provides
    fun provideWeatherInterpretationImpl(): WeatherInterpretation {
        return WeatherInterpretationCodeImpl()
    }

}