package com.stanislavdumchykov.weatherapp.domain.model

import androidx.annotation.StringRes

data class ScreenWeatherModel(
    val temperature: Double,
    val city: String,
    val country: String,
    @StringRes val weatherInterpretationString: Int,
    val windFlow: Double,
    val precipitation: Int,
    val humidity: Int,
)
