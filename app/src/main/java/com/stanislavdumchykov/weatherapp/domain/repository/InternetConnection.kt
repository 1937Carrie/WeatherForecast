package com.stanislavdumchykov.weatherapp.domain.repository

import android.content.Context

interface InternetConnection {
    fun check(context: Context): Boolean
}