package com.stanislavdumchykov.weatherapp.presentation.checkinternet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.stanislavdumchykov.weatherapp.R
import com.stanislavdumchykov.weatherapp.databinding.ActivityCheckInternetBinding
import com.stanislavdumchykov.weatherapp.domain.utils.Status
import com.stanislavdumchykov.weatherapp.presentation.base.BaseActivity
import com.stanislavdumchykov.weatherapp.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CheckInternetActivity :
    BaseActivity<ActivityCheckInternetBinding>(ActivityCheckInternetBinding::inflate) {

    private val viewModel: CheckInternetViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setCollectors()
    }

    private fun setCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.status.collect { status ->
                    when (status) {
                        Status.SUCCESS -> {
                            Log.d(TAG, "SUCCESS!")
                            startNextActivity()
                        }

                        Status.LOADING -> {
                            Log.d(TAG, "LOADING!")
                        }

                        Status.FAILURE -> {
                            Log.d(TAG, "FAILURE!")
                            Toast.makeText(
                                this@CheckInternetActivity,
                                getString(R.string.text_no_connection),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    override fun setListeners() {
        binding.buttonTryConnectionAgain.setOnClickListener {
            Log.d(TAG, "buttonTryConnectionAgain")
            startNextActivity()
        }
    }

    // Start main activity if there is internet
    private fun startNextActivity() {
        if (viewModel.status.value != Status.SUCCESS) return

        startActivity(Intent(this@CheckInternetActivity, MainActivity::class.java))
        finish()
    }

    private companion object {
        private const val TAG = "CheckInternetActivity"
    }
}