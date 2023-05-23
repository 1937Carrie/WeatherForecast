package com.stanislavdumchykov.weatherapp

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.stanislavdumchykov.weatherapp.databinding.ActivityCheckInternetBinding
import com.stanislavdumchykov.weatherapp.presentation.base.BaseActivity
import com.stanislavdumchykov.weatherapp.presentation.main.MainActivity

class CheckInternetActivity :
    BaseActivity<ActivityCheckInternetBinding>(ActivityCheckInternetBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startNextActivity()
    }

    override fun setListeners() {
        super.setListeners()
        binding.buttonTryConnectionAgain.setOnClickListener {
            if (!startNextActivity()) Toast.makeText(
                binding.root.context,
                "Still there is not connection",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun startNextActivity(): Boolean {
        return if (checkInternetConnection(binding.root.context)) {
            startActivity(Intent(binding.root.context, MainActivity::class.java))
            finish()
            true
        } else false
    }

    private fun checkInternetConnection(context: Context): Boolean {
        if (context == null) return false

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }

        return false
    }
}