package com.stanislavdumchykov.weatherapp.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stanislavdumchykov.weatherapp.data.repository.OpenMeteoServiceImpl
import com.stanislavdumchykov.weatherapp.domain.ScreenWeatherModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MainViewModel : ViewModel() {
    private var _data = MutableLiveData<ScreenWeatherModel>()
    val data: LiveData<ScreenWeatherModel> = _data


    fun getCurrentWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            val openMeteoServiceImpl = OpenMeteoServiceImpl
            val response = try {
                openMeteoServiceImpl.service.getForecast()
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }
            if (response.isSuccessful) {
                val screenWeatherModel = ScreenWeatherModel(
                    weatherImage = response.body()?.hourly?.weathercode?.get(12) ?: 0,
                    temperature = response.body()?.hourly?.temperature_2m?.get(12) ?: 0.0,
                    city = "Vinnytsia",
                    weatherInterpretationString = response.body()?.hourly?.weathercode?.get(12)
                        ?: 0,
                )
                _data.postValue(screenWeatherModel)
            }
        }
    }

}