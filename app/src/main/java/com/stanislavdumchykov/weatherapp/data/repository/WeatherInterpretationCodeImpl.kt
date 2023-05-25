package com.stanislavdumchykov.weatherapp.data.repository

import com.stanislavdumchykov.weatherapp.R
import com.stanislavdumchykov.weatherapp.domain.model.WeatherInterpretationData
import com.stanislavdumchykov.weatherapp.domain.repository.WeatherInterpretation

class WeatherInterpretationCodeImpl : WeatherInterpretation {
    private val dataMap = mapOf(
        0 to WeatherInterpretationData(R.string.weather_clear_sky, R.drawable.weather_code0),
        1 to WeatherInterpretationData(R.string.weather_mainly_clear, R.drawable.weather_code1),
        2 to WeatherInterpretationData(R.string.weather_party_cloudy, R.drawable.weather_code2),
        3 to WeatherInterpretationData(R.string.weather_overcast, R.drawable.weather_code3),
        45 to WeatherInterpretationData(R.string.weather_fog, R.drawable.weather_code45),
        48 to WeatherInterpretationData(
            R.string.weather_depositing_rime_fog,
            R.drawable.weather_code48
        ),
        51 to WeatherInterpretationData(R.string.weather_drizzle_light, R.drawable.weather_code51),
        53 to WeatherInterpretationData(
            R.string.weather_drizzle_moderate,
            R.drawable.weather_code53
        ),
        55 to WeatherInterpretationData(R.string.weather_drizzle_dense, R.drawable.weather_code55),
        56 to WeatherInterpretationData(
            R.string.weather_freezing_drizzle_light,
            R.drawable.weather_code56
        ),
        57 to WeatherInterpretationData(
            R.string.weather_freezing_drizzle_dense,
            R.drawable.weather_code57
        ),
        61 to WeatherInterpretationData(R.string.weather_rain_slight, R.drawable.weather_code61),
        63 to WeatherInterpretationData(R.string.weather_rain_moderate, R.drawable.weather_code63),
        65 to WeatherInterpretationData(R.string.weather_rain_heavy, R.drawable.weather_code65),
        66 to WeatherInterpretationData(
            R.string.weather_freezing_rain_light,
            R.drawable.weather_code66
        ),
        67 to WeatherInterpretationData(
            R.string.weather_freezing_rain_heavy,
            R.drawable.weather_code67
        ),
        71 to WeatherInterpretationData(
            R.string.weather_snow_fall_slight,
            R.drawable.weather_code71
        ),
        73 to WeatherInterpretationData(
            R.string.weather_snow_fall_moderate,
            R.drawable.weather_code73
        ),
        75 to WeatherInterpretationData(
            R.string.weather_snow_fall_intensity,
            R.drawable.weather_code75
        ),
        77 to WeatherInterpretationData(R.string.weather_snow_grains, R.drawable.weather_code77),
        80 to WeatherInterpretationData(
            R.string.weather_rain_showers_slight,
            R.drawable.weather_code80
        ),
        81 to WeatherInterpretationData(
            R.string.weather_rain_showers_moderate,
            R.drawable.weather_code81
        ),
        82 to WeatherInterpretationData(
            R.string.weather_rain_showers_violent,
            R.drawable.weather_code82
        ),
        85 to WeatherInterpretationData(
            R.string.weather_snow_showers_slight,
            R.drawable.weather_code85
        ),
        86 to WeatherInterpretationData(
            R.string.weather_snow_showers_heavy,
            R.drawable.weather_code86
        ),
        95 to WeatherInterpretationData(R.string.weather_thunderstorm, R.drawable.weather_code95),
        96 to WeatherInterpretationData(
            R.string.weather_thunderstorm_with_slight_hail,
            R.drawable.weather_code96
        ),
        99 to WeatherInterpretationData(
            R.string.weather_thunderstorm_with_heavy_hail,
            R.drawable.weather_code99
        ),
    )

    override fun getImageByCode(weatherCode: Int): Int {
        return dataMap[weatherCode]?.weatherImage ?: R.drawable.weather_code_unkown
    }

    override fun getDescriptionByCode(weatherCode: Int): Int {
        return dataMap[weatherCode]?.description ?: R.string.weather_unknown
    }
}