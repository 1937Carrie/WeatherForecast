package com.stanislavdumchykov.weatherapp.domain.responseCityLocation

data class CityLocationItem(
    val country: String,
    val is_capital: Boolean,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val population: Int
)