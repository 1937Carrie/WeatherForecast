package com.stanislavdumchykov.weatherapp.domain.model

data class ShortWeatherFormat(
    val temperature: Double,
    val weatherCode: Int,
    val time: String,
)
