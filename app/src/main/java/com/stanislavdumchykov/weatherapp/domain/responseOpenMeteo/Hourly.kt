package com.stanislavdumchykov.weatherapp.domain.responseOpenMeteo

data class Hourly(
    val precipitation_probability: List<Int>,
    val relativehumidity_2m: List<Int>,
    val temperature_2m: List<Double>,
    val time: List<String>,
    val weathercode: List<Int>,
    val windspeed_10m: List<Double>
)