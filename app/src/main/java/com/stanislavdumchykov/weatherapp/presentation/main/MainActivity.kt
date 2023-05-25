package com.stanislavdumchykov.weatherapp.presentation.main

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.stanislavdumchykov.weatherapp.R
import com.stanislavdumchykov.weatherapp.databinding.ActivityMainBinding
import com.stanislavdumchykov.weatherapp.presentation.base.BaseActivity
import com.stanislavdumchykov.weatherapp.presentation.main.adapter.RecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint

private const val LOCATION_PERMISSION_REQUEST_CODE = 1

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private val mainViewModel: MainViewModel by viewModels()
    private val recyclerAdapter: RecyclerAdapter by lazy {
        RecyclerAdapter(binding.recyclerView.width) { i: Int ->
            mainViewModel.getDescriptionByCode(i)
        }
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.recyclerView.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }



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
        when (requestCode) {
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
            recyclerAdapter.submitList(it.toMutableList())
        }
    }

    private fun isConnectToInternet(): Boolean {
        return if (!mainViewModel.isInternetConnect(this)) {
            mainViewModel.setCurrentWeatherFromDatabase()
            false
        } else true
    }

    private fun getDrawableFromViewModel(weatherInterpretationCode: Int): Drawable =
        AppCompatResources.getDrawable(
            applicationContext,
            mainViewModel.getImageByCode(weatherInterpretationCode)
        ) ?: AppCompatResources.getDrawable(applicationContext, R.drawable.weather_code_unkown)!!


    private fun getStringFromViewModel(weatherInterpretationCode: Int): String =
        getString(mainViewModel.getDescriptionByCode(weatherInterpretationCode))

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