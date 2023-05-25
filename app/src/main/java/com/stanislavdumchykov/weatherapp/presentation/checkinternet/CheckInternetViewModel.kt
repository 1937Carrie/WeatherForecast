package com.stanislavdumchykov.weatherapp.presentation.checkinternet

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stanislavdumchykov.weatherapp.data.database.weatherForecast.WeatherForecastDao
import com.stanislavdumchykov.weatherapp.domain.repository.InternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class CheckInternetViewModel @Inject constructor(
    private val dao: WeatherForecastDao,
    private val internetConnection: InternetConnection,
) : ViewModel() {

    fun isDatabaseExists(): Boolean {
        val result = viewModelScope.async(Dispatchers.IO) {
            return@async dao.isExists()
        }

        return runBlocking { result.await() }
    }

    fun isInternetConnect(context: Context): Boolean {
        return internetConnection.check(context)
    }
}