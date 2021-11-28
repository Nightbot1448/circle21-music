package com.makentoshe.androidgithubcitemplate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.R

import android.media.MediaPlayer
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.v(TAG, "Initializing sounds...")

        val mp: MediaPlayer = MediaPlayer.create(this, R.raw.sound)

        val play_button: Button = findViewById<View>(R.id.button) as Button
        play_button.setOnClickListener(object : OnClickListener() {
            fun onClick(v: View?) {
                Log.v(TAG, "Playing sound...")
                mp.start()
            }
        })
    }
}
//123