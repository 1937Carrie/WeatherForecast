package com.stanislavdumchykov.weatherapp.data.database.weatherForecast

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherForecastDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insert(weatherForecast: WeatherForecast)

    @Query("SELECT * FROM WeatherForecast LIMIT 1")
    fun getAll(): WeatherForecast

    @Query("SELECT EXISTS(SELECT * FROM WeatherForecast)")
    fun isExists(): Boolean
}