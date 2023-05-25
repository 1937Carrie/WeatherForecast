package com.stanislavdumchykov.weatherapp.domain.repository

interface WeatherInterpretation {

    fun getImageByCode(weatherCode: Int): Int
    fun getDescriptionByCode(weatherCode: Int): Int

}