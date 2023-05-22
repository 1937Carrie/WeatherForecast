package com.stanislavdumchykov.weatherapp.domain.responseopenmeteo

data class HourlyUnits(
    val precipitation_probability: String,
    val relativehumidity_2m: String,
    val temperature_2m: String,
    val time: String,
    val weathercode: String,
    val windspeed_10m: String
)