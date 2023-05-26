package com.stanislavdumchykov.weatherapp.data.di

import com.stanislavdumchykov.weatherapp.data.network.NetworkStatusTrackerImpl
import com.stanislavdumchykov.weatherapp.domain.network.NetworkStatusTracker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InternetConnectionModule {
    @Singleton
    @Binds
    abstract fun bindNetworkStatusTracker(
        networkStatusTracker: NetworkStatusTrackerImpl
    ): NetworkStatusTracker
}