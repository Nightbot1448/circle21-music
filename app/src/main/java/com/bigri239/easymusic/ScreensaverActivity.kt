package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

@Suppress("DEPRECATION")
class ScreensaverActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        setContentView(R.layout.activity_screensaver)
    }
    override fun onStart() {
        super.onStart()
        Handler().postDelayed({
            val mainIntent = Intent(this@ScreensaverActivity, MainActivity::class.java)
            this@ScreensaverActivity.startActivity(mainIntent)
            this@ScreensaverActivity.finish()
        }, 500)
    }
}