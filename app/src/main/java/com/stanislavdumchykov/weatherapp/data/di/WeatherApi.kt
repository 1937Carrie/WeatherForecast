package com.stanislavdumchykov.weatherapp.data.di

import com.stanislavdumchykov.weatherapp.domain.responseOpenMeteo.ResponseOpenMeteo
import com.stanislavdumchykov.weatherapp.domain.retrofitApi.OpenMeteoService
import retrofit2.Response
import javax.inject.Inject

class WeatherApi @Inject constructor(private val openMeteoService: OpenMeteoService) {
    suspend fun getForecast(latitude: Double, longitude: Double): Response<ResponseOpenMeteo> =
        openMeteoService.getForecast(latitude, longitude)
}