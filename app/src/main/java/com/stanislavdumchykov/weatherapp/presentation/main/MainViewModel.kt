package com.stanislavdumchykov.weatherapp.presentation.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stanislavdumchykov.weatherapp.data.database.weatherForecast.WeatherForecast
import com.stanislavdumchykov.weatherapp.data.database.weatherForecast.WeatherForecastDao
import com.stanislavdumchykov.weatherapp.data.di.CityLocationApi
import com.stanislavdumchykov.weatherapp.data.di.WeatherApi
import com.stanislavdumchykov.weatherapp.domain.model.ScreenWeatherModel
import com.stanislavdumchykov.weatherapp.domain.model.ShortWeatherFormat
import com.stanislavdumchykov.weatherapp.domain.network.NetworkStatusTracker
import com.stanislavdumchykov.weatherapp.domain.repository.WeatherInterpretation
import com.stanislavdumchykov.weatherapp.domain.responseCityLocation.CityLocationItem
import com.stanislavdumchykov.weatherapp.domain.responseOpenMeteo.ResponseOpenMeteo
import com.stanislavdumchykov.weatherapp.domain.utils.NetworkStatus
import com.stanislavdumchykov.weatherapp.domain.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dao: WeatherForecastDao,
    private val weatherApi: WeatherApi,
    private val cityLocationApi: CityLocationApi,
    private val weatherInterpretationData: WeatherInterpretation,
    private val networkStatusTracker: NetworkStatusTracker,

    ) : ViewModel() {
    private var _currentTimeData = MutableLiveData<ScreenWeatherModel>()
    val currentTimeData: LiveData<ScreenWeatherModel> = _currentTimeData

    private var _shortData = MutableLiveData<List<ShortWeatherFormat>>()
    val shortData: LiveData<List<ShortWeatherFormat>> = _shortData

    val status = MutableStateFlow(Status.LOADING)

    private var _city = MutableLiveData<CityLocationItem?>()
    val city: LiveData<CityLocationItem?> = _city

    init {
        viewModelScope.launch {
            networkStatusTracker.networkStatus.collect { networkState ->
                when (networkState) {
                    NetworkStatus.Available -> status.value = Status.SUCCESS
                    else -> status.value = Status.FAILURE
                }
            }
        }
    }

    /**
     * Get weather forecast from internet.
     */
    fun getCurrentWeather(latitude: Double, longitude: Double) {
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
                    city = if (city.value == null) "${response.body()?.latitude}, ${response.body()?.longitude}" else city.value?.name.orEmpty(),
                    country = if (city.value == null) "undefined" else city.value?.country.orEmpty(),
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
    fun setCurrentWeatherFromDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            val cachedWeatherForecast = dao.getAll()

            val screenWeatherModel = ScreenWeatherModel(
                temperature = cachedWeatherForecast.temperature,
                city = cachedWeatherForecast.city,
                country = cachedWeatherForecast.country,
                weatherInterpretationString = cachedWeatherForecast.weatherCode,
                windFlow = cachedWeatherForecast.windFlow,
                precipitation = cachedWeatherForecast.precipitation,
                humidity = cachedWeatherForecast.humidity,
            )

            val listOfShortData =
                getShortDataList(
                    temperatureList = cachedWeatherForecast.temperatureList.substring(
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

    fun getCityCoordinates(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = try {
                cityLocationApi.getCityCoordinates(cityName)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            if (response.isSuccessful && (response.body()?.size ?: 0) > 0) {
                Log.d("LogLog", response.body()!![0].toString())
                _city.postValue(response.body()!![0])
            }
        }
    }

    fun resetCity() {
        _city.postValue(null)
    }

    private fun getWeatherForecast(body: ResponseOpenMeteo?, timePoint: Int): WeatherForecast {
        return WeatherForecast(
            temperature = body?.hourly?.temperature_2m?.get(timePoint) ?: 0.0,
            city = if (city.value == null) "undefined" else city.value?.name.orEmpty(),
            country = if (city.value == null) "undefined" else city.value?.country.orEmpty(),
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
