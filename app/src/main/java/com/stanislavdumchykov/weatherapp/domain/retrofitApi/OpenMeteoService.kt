package com.stanislavdumchykov.weatherapp.domain.retrofitApi

import com.stanislavdumchykov.weatherapp.domain.responseOpenMeteo.ResponseOpenMeteo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoService {
    //    @FormUrlEncoded
    @GET("v1/forecast?hourly=temperature_2m,relativehumidity_2m,precipitation_probability,weathercode&forecast_days=2")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
    ): Response<ResponseOpenMeteo>
}