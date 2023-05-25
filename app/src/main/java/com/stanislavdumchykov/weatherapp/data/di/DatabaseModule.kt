package com.stanislavdumchykov.weatherapp.data.di

import android.content.Context
import androidx.room.Room
import com.stanislavdumchykov.weatherapp.data.database.AppDatabase
import com.stanislavdumchykov.weatherapp.data.database.weatherForecast.WeatherForecastDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "weather_forecast_database"
        ).build()
    }

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): WeatherForecastDao {
        return appDatabase.weatherForecastDao()
    }
}