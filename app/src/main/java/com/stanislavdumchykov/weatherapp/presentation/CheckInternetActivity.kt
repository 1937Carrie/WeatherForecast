package com.stanislavdumchykov.weatherapp.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.stanislavdumchykov.weatherapp.data.database.AppDatabase
import com.stanislavdumchykov.weatherapp.data.repository.InternetConnectionImpl
import com.stanislavdumchykov.weatherapp.databinding.ActivityCheckInternetBinding
import com.stanislavdumchykov.weatherapp.presentation.base.BaseActivity
import com.stanislavdumchykov.weatherapp.presentation.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class CheckInternetActivity :
    BaseActivity<ActivityCheckInternetBinding>(ActivityCheckInternetBinding::inflate) {

    private val database by lazy {
        AppDatabase.getDatabase(this)
    }

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
        return if (InternetConnectionImpl().check(binding.root.context) || isDatabaseExists()) {
            startActivity(Intent(binding.root.context, MainActivity::class.java))
            finish()
            true
        } else false
    }

    private fun isDatabaseExists(): Boolean {
        return runBlocking {
            withContext(Dispatchers.IO) {
                database.weatherForecastDao().isExists()
            }
        }
    }

}