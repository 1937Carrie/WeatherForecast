package com.stanislavdumchykov.weatherapp.domain

data class ShortWeatherFormat(
    val temperature: Double,
    val weatherCode: Int,
    val time: String,
)
