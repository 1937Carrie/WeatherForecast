package com.stanislavdumchykov.weatherapp.domain

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class ScreenWeatherModel(
    @DrawableRes val weatherImage: Int,
    val temperature: Double,
    val city: String,
    @StringRes val weatherInterpretationString: Int,
)
