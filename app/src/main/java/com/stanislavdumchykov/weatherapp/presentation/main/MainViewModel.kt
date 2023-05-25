package com.stanislavdumchykov.weatherapp.presentation.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.stanislavdumchykov.weatherapp.R
import com.stanislavdumchykov.weatherapp.data.database.weatherForecast.WeatherForecast
import com.stanislavdumchykov.weatherapp.data.database.weatherForecast.WeatherForecastDao
import com.stanislavdumchykov.weatherapp.data.di.WeatherApi
import com.stanislavdumchykov.weatherapp.domain.model.ScreenWeatherModel
import com.stanislavdumchykov.weatherapp.domain.model.ShortWeatherFormat
import com.stanislavdumchykov.weatherapp.domain.repository.InternetConnection
import com.stanislavdumchykov.weatherapp.domain.repository.WeatherInterpretation
import com.stanislavdumchykov.weatherapp.domain.responseOpenMeteo.ResponseOpenMeteo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

private const val LOCATION_PERMISSION_REQUEST_CODE = 1

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dao: WeatherForecastDao,
    private val weatherApi: WeatherApi,
    private val weatherInterpretationData: WeatherInterpretation,
    private val internetConnection: InternetConnection,

    ) : ViewModel() {
    private var _currentTimeData = MutableLiveData<ScreenWeatherModel>()
    val currentTimeData: LiveData<ScreenWeatherModel> = _currentTimeData

    private var _shortData = MutableLiveData<List<ShortWeatherFormat>>()
    val shortData: LiveData<List<ShortWeatherFormat>> = _shortData

    /**
     * The field that defines the coordinates.
     */
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    /**
     * Get weather forecast from internet.
     */
    private fun getCurrentWeather(latitude: Float, longitude: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = try {
                weatherApi.getForecast(latitude, longitude)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }
            if (response.isSuccessful) {
                val currentTime = getCurrentTime()
                val timePoint = getRightTime(currentTime)
                val screenWeatherModel = ScreenWeatherModel(
                    temperature = response.body()?.hourly?.temperature_2m?.get(timePoint) ?: 0.0,
                    city = "${response.body()?.latitude}, ${response.body()?.longitude}",
                    weatherInterpretationString = response.body()?.hourly?.weathercode?.get(
                        timePoint
                    ) ?: 0,
                    windFlow = response.body()?.hourly?.windspeed_10m?.get(timePoint) ?: 0.0,
                    precipitation = response.body()?.hourly?.precipitation_probability?.get(
                        timePoint
                    ) ?: 0,
                    humidity = response.body()?.hourly?.relativehumidity_2m?.get(timePoint) ?: 0,
                )
                val listOfShortData = getShortDataList(
                    response.body()?.hourly?.temperature_2m,
                    response.body()?.hourly?.time,
                    response.body()?.hourly?.weathercode,
                    currentTime,
                )
                dao.insert(getWeatherForecast(response.body(), timePoint))
                _currentTimeData.postValue(screenWeatherModel)
                _shortData.postValue(listOfShortData)
            }
        }
    }

    /**
     * Get cached forecast.
     */
    private fun setCurrentWeatherFromDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            val cachedWeatherForecast = dao.getAll()

            val screenWeatherModel = ScreenWeatherModel(
                temperature = cachedWeatherForecast.temperature,
                city = cachedWeatherForecast.city,
                weatherInterpretationString = cachedWeatherForecast.weatherCode,
                windFlow = cachedWeatherForecast.windFlow,
                precipitation = cachedWeatherForecast.precipitation,
                humidity = cachedWeatherForecast.humidity,
            )

            val listOfShortData =
                getShortDataList(temperatureList = cachedWeatherForecast.temperatureList.substring(
                    1, cachedWeatherForecast.temperatureList.length - 1
                ).split(Regex("(, )+")).map { it.toDouble() },
                    timeList = cachedWeatherForecast.timeList.substring(
                        1, cachedWeatherForecast.timeList.length - 1
                    ).filterNot { it.isWhitespace() }.split(","),
                    weatherCodeList = cachedWeatherForecast.weatherCodeList.substring(
                        1, cachedWeatherForecast.weatherCodeList.length - 1
                    ).filterNot { it.isWhitespace() }.split(",").map { it.toInt() },
                    time = getCurrentTime()
                )

            _currentTimeData.postValue(screenWeatherModel)
            _shortData.postValue(listOfShortData)
        }
    }

    fun getImageByCode(weatherCode: Int): Int {
        return weatherInterpretationData.getImageByCode(weatherCode)
    }

    fun getDescriptionByCode(weatherCode: Int): Int {
        return weatherInterpretationData.getDescriptionByCode(weatherCode)
    }

    private fun isInternetConnect(context: Context): Boolean {
        return internetConnection.check(context)
    }

    fun getCurrentLocation(context: Context) {
        if (checkLocationPermissions(context) && isConnectToInternet(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestLocationPermissions(context as Activity)
                return
            }
            if (!this::fusedLocationClient.isInitialized) fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                // Use the location object
                if (location != null) {
                    val latitude = location.latitude.toFloat()
                    val longitude = location.longitude.toFloat()

                    getCurrentWeather(latitude, longitude)

                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.text_failed_to_determine_coordinates),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener { exception ->
                // Handle any errors that occurred during location retrieval
            }
        }
    }

    /**
     * Check connection to internet.
     */
    fun isConnectToInternet(context: Context): Boolean {
        return if (!isInternetConnect(context)) {
            setCurrentWeatherFromDatabase()
            Toast.makeText(
                context,
                context.getString(R.string.text_there_is_no_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
            false
        } else true
    }

    fun requestLocationPermissions(context: Activity) {
        ActivityCompat.requestPermissions(
            context, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun checkLocationPermissions(context: Context): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED && coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun getWeatherForecast(body: ResponseOpenMeteo?, timePoint: Int): WeatherForecast {
        return WeatherForecast(
            temperature = body?.hourly?.temperature_2m?.get(timePoint) ?: 0.0,
            city = "${body?.latitude}, ${body?.longitude}",
            weatherCode = body?.hourly?.weathercode?.get(timePoint) ?: 0,
            windFlow = body?.hourly?.windspeed_10m?.get(timePoint) ?: 0.0,
            precipitation = body?.hourly?.precipitation_probability?.get(timePoint) ?: 0,
            humidity = body?.hourly?.relativehumidity_2m?.get(timePoint) ?: 0,
            temperatureList = (body?.hourly?.temperature_2m ?: emptyList()).toString(),
            timeList = (body?.hourly?.time ?: emptyList()).toString(),
            weatherCodeList = (body?.hourly?.weathercode ?: emptyList()).toString(),
        )
    }

    private fun getShortDataList(
        temperatureList: List<Double>?,
        timeList: List<String>?,
        weatherCodeList: List<Int>?,
        time: String
    ): List<ShortWeatherFormat> {
        val hour = time.substring(0, 2).toInt()
        val mutableListOf = mutableListOf<ShortWeatherFormat>()

        for (i in hour..hour + 4) {
            mutableListOf.add(
                ShortWeatherFormat(
                    temperatureList?.get(i) ?: 0.0,
                    weatherCodeList?.get(i) ?: -1,
                    timeList?.get(i) ?: ""
                )
            )
        }

        return mutableListOf
    }

    private fun getCurrentTime(): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    private fun getRightTime(time: String): Int {
        val timeList = time.split(':').map { it.toInt() }
        val hours = timeList[0]
        val minutes = timeList[1]
        return if (minutes >= 30) hours + 1 else hours
    }
}
