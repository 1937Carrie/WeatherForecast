package com.stanislavdumchykov.weatherapp.data.di

import com.stanislavdumchykov.weatherapp.domain.retrofitApi.OpenMeteoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val URL = "https://api.open-meteo.com/"

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

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
    fun serverApi(okHttpClient: OkHttpClient): OpenMeteoService {
        return Retrofit.Builder().client(okHttpClient).baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(OpenMeteoService::class.java)
    }

}