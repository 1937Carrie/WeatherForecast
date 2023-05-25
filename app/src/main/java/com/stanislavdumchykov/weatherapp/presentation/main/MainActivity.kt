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
import com.stanislavdumchykov.weatherapp.R
import com.stanislavdumchykov.weatherapp.databinding.ActivityMainBinding
import com.stanislavdumchykov.weatherapp.presentation.base.BaseActivity
import com.stanislavdumchykov.weatherapp.presentation.main.adapter.RecyclerAdapter
import com.stanislavdumchykov.weatherapp.presentation.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private val mainViewModel: MainViewModel by viewModels()
    private val recyclerAdapter: RecyclerAdapter by lazy {
        RecyclerAdapter(binding.recyclerView.width) { weatherCode: Int ->
            mainViewModel.getDescriptionByCode(weatherCode) // Realisation how translate weather integer code to string description
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeRecyclerView()
        getWeatherForecast()
    }

    // Getting a forecast by accepting the permission to use the location.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (
            requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mainViewModel.getCurrentLocation(this)
        }
    }

    override fun setListeners() {
        // Swipe-to-refresh layout listener
        binding.swiperefresh.setOnRefreshListener {
            mainViewModel.getCurrentLocation(this)
            binding.swiperefresh.isRefreshing = false
        }
    }

    override fun setObservers() {
        // Observer for data on top block
        mainViewModel.currentTimeData.observe(this) {
            with(binding) {
                imageViewWeatherInterpretationImage.setImageDrawable(getDrawableFromViewModel(it.weatherInterpretationString))
                textViewDegreesCelsius.text = getString(R.string.temperature_value, it.temperature)
                textViewCity.text = it.city
                textViewWeatherInterpretationString.text =
                    getWeatherInterpretation(it.weatherInterpretationString)
                textViewWindFlowValue.text = it.windFlow.toString()
                textViewPreceptionValue.text =
                    getString(R.string.precipitation_value, it.precipitation)
                textViewHumidityValue.text = getString(R.string.humidity_value, it.humidity)
            }
        }
        // Observer for data on bottom block
        mainViewModel.shortData.observe(this) {
            recyclerAdapter.submitList(it.toMutableList())
        }
    }

    private fun initializeRecyclerView() {
        binding.recyclerView.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
    }

    /**
     * Get weather forecast if permission is granted.
     */
    private fun getWeatherForecast() {
        if (mainViewModel.currentTimeData.value != null || !mainViewModel.isConnectToInternet(this)) {
            return
        }

        if (checkLocationPermissions()) {
            // Permissions already granted, proceed with getting the location
            mainViewModel.getCurrentLocation(this)
        } else {
            mainViewModel.requestLocationPermissions(this)
        }
    }

    /**
     * Convert integer code of weather to drawable resource.
     */
    private fun getDrawableFromViewModel(weatherInterpretationCode: Int): Drawable =
        AppCompatResources.getDrawable(
            applicationContext,
            mainViewModel.getImageByCode(weatherInterpretationCode)
        ) ?: AppCompatResources.getDrawable(applicationContext, R.drawable.weather_code_unkown)!!

    private fun getWeatherInterpretation(weatherInterpretationCode: Int): String =
        getString(mainViewModel.getDescriptionByCode(weatherInterpretationCode)) // getString(mainViewModel.getDescriptionByCode(weatherInterpretationCode)) is to long so this method a little bit reduces it.

    /**
     * Checks permission for access to geolocation.
     */
    private fun checkLocationPermissions(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED && coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

}