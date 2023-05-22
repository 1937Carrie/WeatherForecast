package com.stanislavdumchykov.weatherapp.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stanislavdumchykov.weatherapp.data.repository.OpenMeteoServiceImpl
import com.stanislavdumchykov.weatherapp.domain.ScreenWeatherModel
import com.stanislavdumchykov.weatherapp.domain.ShortWeatherFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainViewModel : ViewModel() {
    private var _currentTimeData = MutableLiveData<ScreenWeatherModel>()
    val currentTimeData: LiveData<ScreenWeatherModel> = _currentTimeData

    private var _shortData = MutableLiveData<List<ShortWeatherFormat>>()
    val shortData: LiveData<List<ShortWeatherFormat>> = _shortData


    fun getCurrentWeather(latitude: Float, longitude: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            val openMeteoServiceImpl = OpenMeteoServiceImpl
            val response = try {
                openMeteoServiceImpl.service.getForecast(latitude, longitude)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }
            if (response.isSuccessful) {
                val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val currentTime = dateFormat.format(Date())
                val time = currentTime.split(':').map { it.toInt() }
                val timePoint = getRightTime(time)
                println("Current time is: $timePoint")
                println(response.body()?.hourly?.time.toString())
                println(response.body()?.hourly?.temperature_2m.toString())
                val screenWeatherModel = ScreenWeatherModel(
                    weatherImage = response.body()?.hourly?.weathercode?.get(timePoint) ?: 0,
                    temperature = response.body()?.hourly?.temperature_2m?.get(timePoint) ?: 0.0,
                    city = "Vinnytsia",
                    weatherInterpretationString = response.body()?.hourly?.weathercode?.get(12)
                        ?: 0,
                    windFlow = response.body()?.hourly?.windspeed_10m?.get(timePoint) ?: 0.0,
                    preception = response.body()?.hourly?.precipitation_probability?.get(timePoint)
                        ?: 0,
                    humidity = response.body()?.hourly?.relativehumidity_2m?.get(timePoint) ?: 0,
                )
                val listOfShortData = getShortDataList(
                    response.body()?.hourly?.temperature_2m,
                    response.body()?.hourly?.time,
                    response.body()?.hourly?.weathercode,
                    currentTime,
                )
                _currentTimeData.postValue(screenWeatherModel)
                _shortData.postValue(listOfShortData)
            }
        }
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

    private fun getRightTime(time: List<Int>): Int {
        val hours = time[0]
        val minutes = time[1]
        return if (minutes >= 30) hours + 1 else hours
    }

}