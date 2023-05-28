package com.stanislavdumchykov.weatherapp.data.di

import com.stanislavdumchykov.weatherapp.domain.responseCityLocation.ResponseCityLocation
import com.stanislavdumchykov.weatherapp.domain.retrofitApi.CityLocationService
import retrofit2.Response
import javax.inject.Inject

class CityLocationApi @Inject constructor(private val cityLocationService: CityLocationService) {
    suspend fun getCityCoordinates(cityName: String): Response<ResponseCityLocation> =
        cityLocationService.getCoordinates(cityName)
}