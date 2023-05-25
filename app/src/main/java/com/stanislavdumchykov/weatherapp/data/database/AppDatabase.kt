package com.stanislavdumchykov.weatherapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.stanislavdumchykov.weatherapp.data.database.weatherForecast.WeatherForecast
import com.stanislavdumchykov.weatherapp.data.database.weatherForecast.WeatherForecastDao

@Database(entities = arrayOf(WeatherForecast::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherForecastDao(): WeatherForecastDao

}