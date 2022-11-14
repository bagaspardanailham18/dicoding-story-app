package com.dicoding.intermediate.storyapp.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.dicoding.intermediate.storyapp.data.DataStoreViewModel
import com.dicoding.intermediate.storyapp.databinding.ActivitySplashScreenBinding
import com.dicoding.intermediate.storyapp.ui.main.MainActivity
import com.dicoding.intermediate.storyapp.ui.auth.AuthActivity
import com.dicoding.intermediate.storyapp.ui.main.MainActivity.Companion.EXTRA_TOKEN
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    private val dataStoreViewModel: DataStoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        determiningDirection()
    }

    private fun determiningDirection() {
        lifecycleScope.launchWhenCreated {
            delay(3000)
            dataStoreViewModel.getAuthToken().observe(this@SplashScreenActivity) { token ->
                if (token.isNullOrEmpty() || token == "") {
                    Log.d("token", "token empty")
                    startActivity(Intent(this@SplashScreenActivity, AuthActivity::class.java))
                    finish()
                } else {
                    Log.d("token", "token : $token")
                    startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java).putExtra(EXTRA_TOKEN, token))
                    finish()
                }
            }
        }
    }
}