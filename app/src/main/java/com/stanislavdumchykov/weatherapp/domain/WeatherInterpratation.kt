package com.stanislavdumchykov.weatherapp.domain

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class WeatherInterpretation(
    @StringRes val description: Int,
    @DrawableRes val weatherImage: Int,
)
