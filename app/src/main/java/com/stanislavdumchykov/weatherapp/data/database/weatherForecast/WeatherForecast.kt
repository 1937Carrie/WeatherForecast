package com.stanislavdumchykov.weatherapp.data.database.weatherForecast

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WeatherForecast(
    @PrimaryKey val uid: Int = 0,
    @ColumnInfo(name = "temperature") val temperature: Double,
    @ColumnInfo(name = "city") val city: String,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "weather_code") val weatherCode: Int,
    @ColumnInfo(name = "wind_flow") val windFlow: Double,
    @ColumnInfo(name = "precipitation") val precipitation: Int,
    @ColumnInfo(name = "humidity") val humidity: Int,
    @ColumnInfo(name = "temperature_list") val temperatureList: String,
    @ColumnInfo(name = "time_list") val timeList: String,
    @ColumnInfo(name = "weather_code_list") val weatherCodeList: String,
)
