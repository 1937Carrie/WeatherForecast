package com.stanislavdumchykov.weatherapp.presentation.main

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.stanislavdumchykov.weatherapp.R
import com.stanislavdumchykov.weatherapp.data.database.AppDatabase
import com.stanislavdumchykov.weatherapp.data.repository.InternetConnectionImpl
import com.stanislavdumchykov.weatherapp.data.repository.WeatherInterpretationCodeImpl
import com.stanislavdumchykov.weatherapp.databinding.ActivityMainBinding
import com.stanislavdumchykov.weatherapp.presentation.base.BaseActivity

private const val LOCATION_PERMISSION_REQUEST_CODE = 1

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private val database by lazy {
        AppDatabase.getDatabase(this)
    }
    private val mainViewModel: MainViewModel by viewModels(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainViewModel(database.weatherForecastDao()) as T
                }
            }
        }
    )
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getWeatherForecast()

        setObservers()
        setListeners()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) getCurrentLocation()
            }
        }
    }

    private fun getWeatherForecast() {
        if (mainViewModel.currentTimeData.value != null || !isConnectToInternet()) {
            return
        }

        if (checkLocationPermissions()) {
            // Permissions already granted, proceed with getting the location
            getCurrentLocation()
        } else {
            requestLocationPermissions()
        }

    }

    override fun setObservers() {
        mainViewModel.currentTimeData.observe(this) {
            with(binding) {
                imageViewWeatherInterpretationImage.setImageDrawable(getDrawableFromViewModel(it.weatherInterpretationString))
                textViewDegreesCelsius.text = getString(R.string.temperature_value, it.temperature)
                textViewCity.text = it.city
                textViewWeatherInterpretationString.text =
                    getStringFromViewModel(it.weatherInterpretationString)
                textViewWindFlowValue.text = it.windFlow.toString()
                textViewPreceptionValue.text =
                    getString(R.string.precipitation_value, it.precipitation)
                textViewHumidityValue.text = getString(R.string.humidity_value, it.humidity)
            }
        }
        mainViewModel.shortData.observe(this) {
            with(binding) {
                textViewWeatherHourlyTemperature1.text =
                    getString(R.string.temperature_value_short, it[0].temperature)
                textViewWeatherHourlyDescription1.text = getStringFromViewModel(it[0].weatherCode)
                textViewWeatherHourlyTime1.text = it[0].time.substring(it[0].time.length - 5)
                textViewWeatherHourlyTemperature2.text =
                    getString(R.string.temperature_value_short, it[1].temperature)
                textViewWeatherHourlyDescription2.text = getStringFromViewModel(it[1].weatherCode)
                textViewWeatherHourlyTime2.text = it[1].time.substring(it[1].time.length - 5)
                textViewWeatherHourlyTemperature3.text =
                    getString(R.string.temperature_value_short, it[2].temperature)
                textViewWeatherHourlyDescription3.text = getStringFromViewModel(it[2].weatherCode)
                textViewWeatherHourlyTime3.text = it[2].time.substring(it[2].time.length - 5)
                textViewWeatherHourlyTemperature4.text =
                    getString(R.string.temperature_value_short, it[3].temperature)
                textViewWeatherHourlyDescription4.text = getStringFromViewModel(it[3].weatherCode)
                textViewWeatherHourlyTime4.text = it[3].time.substring(it[3].time.length - 5)
                textViewWeatherHourlyTemperature5.text =
                    getString(R.string.temperature_value_short, it[4].temperature)
                textViewWeatherHourlyDescription5.text = getStringFromViewModel(it[4].weatherCode)
                textViewWeatherHourlyTime5.text = it[4].time.substring(it[4].time.length - 5)
            }
        }
    }

    private fun isConnectToInternet(): Boolean {
        return if (!InternetConnectionImpl().check(this)) {
            mainViewModel.setCurrentWeatherFromDatabase()
            false
        } else true
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
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED && coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun getCurrentLocation() {
        if (checkLocationPermissions()) {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
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

                } else {
                    val latitude = 49.23f
                    val longitude = 28.47f

                    mainViewModel.getCurrentWeather(latitude, longitude)
                }
            }.addOnFailureListener { exception ->
                // Handle any errors that occurred during location retrieval
            }
        }
    }
}