package com.stanislavdumchykov.weatherapp.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class WeatherInterpretationData(
    @StringRes val description: Int,
    @DrawableRes val weatherImage: Int,
)
