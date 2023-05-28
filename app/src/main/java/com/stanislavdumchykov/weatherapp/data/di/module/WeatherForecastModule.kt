package com.stanislavdumchykov.weatherapp.data.di.module

import com.stanislavdumchykov.weatherapp.domain.retrofitApi.CityLocationService
import com.stanislavdumchykov.weatherapp.domain.retrofitApi.OpenMeteoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherForecastModule {
    @Provides
    @Named("Weather")
    fun provideWeatherBaseUrl(): String = "https://api.open-meteo.com/"

    @Provides
    @Named("CityLocation")
    fun provideCityLocationBaseUrl(): String = "https://api.api-ninjas.com/"

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    @Singleton
    @Provides
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build()
    }

    @Singleton
    @Provides
    fun weatherApi(
        @Named("Weather") BASE_URL: String,
        okHttpClient: OkHttpClient
    ): OpenMeteoService {
        return Retrofit.Builder().client(okHttpClient).baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(OpenMeteoService::class.java)
    }

    @Singleton
    @Provides
    fun cityLocationApi(
        @Named("CityLocation") BASE_URL: String,
        okHttpClient: OkHttpClient
    ): CityLocationService {
        return Retrofit.Builder().client(okHttpClient).baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(CityLocationService::class.java)
    }
}