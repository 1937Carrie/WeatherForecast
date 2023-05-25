package com.stanislavdumchykov.weatherapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.stanislavdumchykov.weatherapp.data.database.weatherForecast.WeatherForecast
import com.stanislavdumchykov.weatherapp.data.database.weatherForecast.WeatherForecastDao

@Database(entities = arrayOf(WeatherForecast::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherForecastDao(): WeatherForecastDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "weather_forecast_database"
                )
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}