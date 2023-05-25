package com.stanislavdumchykov.weatherapp.presentation.checkinternet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.stanislavdumchykov.weatherapp.R
import com.stanislavdumchykov.weatherapp.databinding.ActivityCheckInternetBinding
import com.stanislavdumchykov.weatherapp.presentation.base.BaseActivity
import com.stanislavdumchykov.weatherapp.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckInternetActivity :
    BaseActivity<ActivityCheckInternetBinding>(ActivityCheckInternetBinding::inflate) {

    private val checkInternetViewModel: CheckInternetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startNextActivity()
    }

    override fun setListeners() {
        super.setListeners()
        binding.buttonTryConnectionAgain.setOnClickListener {
            if (!startNextActivity()) Toast.makeText(
                binding.root.context, getString(R.string.text_no_connection), Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Start main activity if there is internet
    private fun startNextActivity(): Boolean {
        with(checkInternetViewModel) {
            return if (isInternetConnect(binding.root.context) || isDatabaseExists()) {
                startActivity(Intent(binding.root.context, MainActivity::class.java))
                finish()
                true
            } else false
        }
    }

}