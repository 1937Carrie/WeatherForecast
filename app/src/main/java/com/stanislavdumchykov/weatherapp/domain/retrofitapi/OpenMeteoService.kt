package com.stanislavdumchykov.weatherapp.domain.retrofitapi

import com.stanislavdumchykov.weatherapp.domain.responseopenmeteo.ResponseOpenMeteo
import retrofit2.Response
import retrofit2.http.GET

interface OpenMeteoService {
    @GET("v1/forecast?latitude=49.23&longitude=28.47&hourly=temperature_2m,relativehumidity_2m,precipitation_probability,weathercode")
    suspend fun getForecast(): Response<ResponseOpenMeteo>
}