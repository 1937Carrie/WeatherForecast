package com.stanislavdumchykov.weatherapp.presentation.main

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.stanislavdumchykov.weatherapp.R
import com.stanislavdumchykov.weatherapp.data.repository.WeatherInterpretationCodeImpl
import com.stanislavdumchykov.weatherapp.databinding.ActivityMainBinding

private const val LOCATION_PERMISSION_REQUEST_CODE = 1

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (checkLocationPermissions()) {
            // Permissions already granted, proceed with getting the location
            getCurrentLocation()
        } else {
            requestLocationPermissions()
        }


        setObservers()
        setListeners()
    }

    private fun setObservers() {
        mainViewModel.currentTimeData.observe(this) {
            with(binding) {
                imageViewWeatherInterpretationImage.setImageDrawable(getDrawableFromViewModel(it.weatherImage))
                textViewDegreesCelsius.text = "${it.temperature}° C"
                textViewCity.text = it.city
                textViewWeatherInterpretationString.text =
                    getStringFromViewModel(it.weatherInterpretationString)
                textViewWindFlowValue.text = it.windFlow.toString()
                textViewPreceptionValue.text = it.preception.toString()
                textViewHumidityValue.text = it.humidity.toString()
            }
        }
        mainViewModel.shortData.observe(this) {
            with(binding) {
                textViewWeatherHourlyTemperature1.text = "${it[0].temperature}°"
                textViewWeatherHourlyDescription1.text = getStringFromViewModel(it[0].weatherCode)
                textViewWeatherHourlyTime1.text = it[0].time.substring(it[0].time.length - 5)
                textViewWeatherHourlyTemperature2.text = "${it[1].temperature}°"
                textViewWeatherHourlyDescription2.text = getStringFromViewModel(it[1].weatherCode)
                textViewWeatherHourlyTime2.text = it[1].time.substring(it[1].time.length - 5)
                textViewWeatherHourlyTemperature3.text = "${it[2].temperature}°"
                textViewWeatherHourlyDescription3.text = getStringFromViewModel(it[2].weatherCode)
                textViewWeatherHourlyTime3.text = it[2].time.substring(it[2].time.length - 5)
                textViewWeatherHourlyTemperature4.text = "${it[3].temperature}°"
                textViewWeatherHourlyDescription4.text = getStringFromViewModel(it[3].weatherCode)
                textViewWeatherHourlyTime4.text = it[3].time.substring(it[3].time.length - 5)
                textViewWeatherHourlyTemperature5.text = "${it[4].temperature}°"
                textViewWeatherHourlyDescription5.text = getStringFromViewModel(it[4].weatherCode)
                textViewWeatherHourlyTime5.text = it[4].time.substring(it[4].time.length - 5)
            }
        }
    }

    private fun setListeners() {

    }

    private fun getDrawableFromViewModel(weatherInterpretationCode: Int): Drawable =
        AppCompatResources.getDrawable(
            applicationContext,
            WeatherInterpretationCodeImpl.getImageByCode(weatherInterpretationCode)
        ) ?: AppCompatResources.getDrawable(applicationContext, R.drawable.weather_code_unkown)!!


    private fun getStringFromViewModel(weatherInterpretationCode: Int): String =
        getString(WeatherInterpretationCodeImpl.getDescriptionByCode(weatherInterpretationCode))

    private fun checkLocationPermissions(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun getCurrentLocation() {
        if (checkLocationPermissions()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestLocationPermissions()
                return
            }
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                // Use the location object
                if (location != null) {
                    val latitude = location.latitude.toFloat()
                    val longitude = location.longitude.toFloat()

                    mainViewModel.getCurrentWeather(latitude, longitude)

                }
            }
                .addOnFailureListener { exception ->
                    // Handle any errors that occurred during location retrieval
                }
        }
    }
}