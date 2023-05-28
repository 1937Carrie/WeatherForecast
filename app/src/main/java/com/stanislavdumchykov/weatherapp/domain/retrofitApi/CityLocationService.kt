package com.stanislavdumchykov.weatherapp.domain.retrofitApi

import com.stanislavdumchykov.weatherapp.domain.responseCityLocation.ResponseCityLocation
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface CityLocationService {
    @Headers("X-Api-Key: UQC1tOXA7G9ZXSxSoffEeA==BxL1h1JN4Uu9NdEy")
    @GET("v1/city")
    suspend fun getCoordinates(
        @Query("name") cityName: String,
    ): Response<ResponseCityLocation>
}