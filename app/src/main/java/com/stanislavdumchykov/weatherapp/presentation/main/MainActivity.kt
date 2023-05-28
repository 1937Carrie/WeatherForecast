package com.stanislavdumchykov.weatherapp.presentation.main

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Task
import com.stanislavdumchykov.weatherapp.R
import com.stanislavdumchykov.weatherapp.databinding.ActivityMainBinding
import com.stanislavdumchykov.weatherapp.domain.utils.Status
import com.stanislavdumchykov.weatherapp.presentation.base.BaseActivity
import com.stanislavdumchykov.weatherapp.presentation.main.adapter.RecyclerAdapter
import com.stanislavdumchykov.weatherapp.presentation.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private val mainViewModel: MainViewModel by viewModels()
    private val recyclerAdapter: RecyclerAdapter by lazy {
        RecyclerAdapter { weatherCode: Int ->
            mainViewModel.getDescriptionByCode(weatherCode) // Realisation how translate weather integer code to string description
        }
    }

    /**
     * The field that defines the coordinates.
     */
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeSearchView()
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
            getCurrentLocation()
        }
    }

    override fun setListeners() {
        // Swipe-to-refresh layout listener
        swipeRefreshSetListener()
    }

    private fun swipeRefreshSetListener() {
        binding.swiperefresh.setOnRefreshListener {
            getCurrentLocation()
            binding.swiperefresh.isRefreshing = false
        }
    }

    override fun setObservers() {
        // Observer for data on top block
        mainViewModel.currentTimeData.observe(this) {
            with(binding) {
                imageViewWeatherInterpretationImage.setImageDrawable(getDrawableFromViewModel(it.weatherInterpretationString))
                textViewDegreesCelsius.text = getString(R.string.temperature_value, it.temperature)
                textViewCity.text = getString(R.string.location, it.city, it.country)
                textViewWeatherInterpretationString.text =
                    getWeatherInterpretation(it.weatherInterpretationString)
                textViewWindFlowValue.text = it.windFlow.toString()
                textViewPreceptionValue.text =
                    getString(R.string.precipitation_value, it.precipitation)
                textViewHumidityValue.text = getString(R.string.humidity_value, it.humidity)
            }
            mainViewModel.resetCity()
        }
        // Observer for data on bottom block
        mainViewModel.shortData.observe(this) {
            recyclerAdapter.submitList(it.toMutableList())
        }
        mainViewModel.city.observe(this) { city ->
            if (city != null) {
                mainViewModel.getCurrentWeather(city.latitude, city.longitude)
            }
        }
    }

    private fun initializeSearchView() {
        binding.imageViewSearch.setOnSearchClickListener {
            it.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
        binding.imageViewSearch.setOnCloseListener {
            binding.imageViewSearch.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            false
        }
        binding.imageViewSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) mainViewModel.getCityCoordinates(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
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
        if (mainViewModel.currentTimeData.value != null || !isConnectToInternet()) return

        getCurrentLocation()
    }

    /**
     * Convert integer code of weather to drawable resource.
     */
    private fun getDrawableFromViewModel(weatherInterpretationCode: Int): Drawable =
        AppCompatResources.getDrawable(
            applicationContext,
            mainViewModel.getImageByCode(weatherInterpretationCode)
        ) ?: AppCompatResources.getDrawable(
            applicationContext,
            R.drawable.weather_code_unkown
        )!!

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

    /**
     * Check connection to internet.
     */
    private fun isConnectToInternet(): Boolean {
        return if (!isInternetConnect()) {
            mainViewModel.setCurrentWeatherFromDatabase()
            showToast()
            false
        } else true
    }

    private fun isInternetConnect(): Boolean {
        return true
    }

    private fun getCurrentLocation() {
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
        if (mainViewModel.status.value == Status.FAILURE) {
            mainViewModel.setCurrentWeatherFromDatabase()
            showToast()
        }
        if (!this::fusedLocationClient.isInitialized) fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)
        val locationTask: Task<Location> =
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            )

        locationTask.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                mainViewModel.getCurrentWeather(latitude, longitude)
                // Use the obtained location
                // Do something with latitude and longitude
            }
        }

        locationTask.addOnFailureListener { exception: Exception ->
            // Handle failure
            // Failed to get location
        }
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun showToast() {
        Toast.makeText(
            this,
            this.getString(R.string.text_there_is_no_internet_connection),
            Toast.LENGTH_SHORT
        ).show()
    }

}
