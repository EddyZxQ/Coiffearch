package com.example.coiffearch

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import androidx.core.content.ContextCompat
import com.example.coiffearch.databinding.ActivityCoiffearchSplashBinding


class CoiffearchSplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoiffearchSplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoiffearchSplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logoSplash.animate().apply {
            duration = 700
            scaleXBy(0.2f)
            scaleYBy(0.2f)
        }


        binding.logoText.animate().apply {
            duration = 700
            scaleXBy(0.2f)
            scaleYBy(0.2f)

        }

        cambiarActivity()

    }

    private fun cambiarActivity(){
        object : CountDownTimer(800,100){
            override fun onTick(p0: Long) {}

            override fun onFinish() {
                startActivity(Intent(this@CoiffearchSplashActivity,LoginActivity::class.java))
                finish()
            }
        }.start()
    }
}