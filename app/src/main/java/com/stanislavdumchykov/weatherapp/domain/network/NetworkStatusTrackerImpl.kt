package com.stanislavdumchykov.weatherapp.domain.network

import com.stanislavdumchykov.weatherapp.domain.utils.NetworkStatus
import kotlinx.coroutines.flow.StateFlow

interface NetworkStatusTracker {
    val networkStatus: StateFlow<NetworkStatus>
}