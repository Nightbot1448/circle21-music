package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_faq.*

@Suppress("DEPRECATION")
class FaqActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)
    }
    override fun onStart() {
        super.onStart()
        val intent = Intent(this, HelpActivity::class.java)
        findViewById<TextView>(R.id.backfaq).setOnClickListener {
            backfaq.isClickable = false
            startActivity(intent)
        }
        text_view.text = "1. Q: Is it possible to save the sound to the device in mp3 or other audio format? \n" +
                "A: Unfortunately, no, the application was created for educational purposes and aims to explain in simple terms the principle of creating music from sounds and understand if you want to do it. However, you can share music with your friends inside the app by uploading projects to your account. Or, if desired, record music using the built-in screen recording application on your phone, and then extract the audio track from the video in the online service. \n\n" +
                "2. Q: What sounds can be uploaded to the app? \n" +
                "A: Sounds in mp3, ogg, wav, aac, 3gp or flac format are suitable for download. The length of the sound must not exceed 5.6 seconds. \n\n" +
                "3. Q: What should I do if the audio tracks on the screen have shifted relative to each other? \n" +
                "A: Scroll all the way to the end or start of the project. To avoid this error, do not scroll audio tracks too abruptly."
    }
}