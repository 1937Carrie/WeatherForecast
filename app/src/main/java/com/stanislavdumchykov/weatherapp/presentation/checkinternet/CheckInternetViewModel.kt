package com.stanislavdumchykov.weatherapp.presentation.checkinternet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stanislavdumchykov.weatherapp.data.database.weatherForecast.WeatherForecastDao
import com.stanislavdumchykov.weatherapp.domain.network.NetworkStatusTracker
import com.stanislavdumchykov.weatherapp.domain.utils.NetworkStatus
import com.stanislavdumchykov.weatherapp.domain.utils.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CheckInternetViewModel @Inject constructor(
    private val dao: WeatherForecastDao,
    networkStatusTracker: NetworkStatusTracker,
) : ViewModel() {

    private val doesDatabaseExistFlow: Flow<Boolean> = dao.doesExist()

    val status: StateFlow<Status> = networkStatusTracker.networkStatus.combine(
        doesDatabaseExistFlow
    ) { networkState, doesDatabaseExist ->
        when {
            networkState == NetworkStatus.Available || doesDatabaseExist -> {
                Status.SUCCESS
            }
            else -> Status.FAILURE
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, Status.LOADING)
}