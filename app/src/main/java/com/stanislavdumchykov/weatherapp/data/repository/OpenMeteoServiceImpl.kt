package com.stanislavdumchykov.weatherapp.data.repository

import com.stanislavdumchykov.weatherapp.domain.retrofitApi.OpenMeteoService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OpenMeteoServiceImpl {
    private val httpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build()

    var retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    var service: OpenMeteoService = retrofit.create(OpenMeteoService::class.java)
}