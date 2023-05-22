package com.stanislavdumchykov.weatherapp.presentation.main

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.stanislavdumchykov.weatherapp.R
import com.stanislavdumchykov.weatherapp.data.repository.WeatherInterpretationCodeImpl
import com.stanislavdumchykov.weatherapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setObservers()
        setListeners()
    }

    private fun setObservers() {
        mainViewModel.data.observe(this) {
            with(binding) {
                imageViewWeatherInterpretationImage.setImageDrawable(getDrawableFromViewModel(it.weatherImage))
                textViewDegreesCelsius.text = "${it.temperature}° C"
                textViewCity.text = it.city
                textViewWeatherInterpretationString.text =
                    getStringFromViewModel(it.weatherInterpretationString)
            }
        }
    }

    private fun setListeners() {
        mainViewModel.getCurrentWeather()
    }

    private fun getDrawableFromViewModel(weatherInterpretationCode: Int): Drawable =
        AppCompatResources.getDrawable(
            applicationContext,
            WeatherInterpretationCodeImpl.getImageByCode(weatherInterpretationCode)
        ) ?: AppCompatResources.getDrawable(applicationContext, R.drawable.weather_code_unkown)!!


    private fun getStringFromViewModel(weatherInterpretationCode: Int): String =
        getString(WeatherInterpretationCodeImpl.getDescriptionByCode(weatherInterpretationCode))
}