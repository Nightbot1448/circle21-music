package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_tutorial.*

@Suppress("DEPRECATION")
class TutorialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)
        val textView = findViewById<View>(R.id.text_vie) as TextView
        textView.movementMethod = ScrollingMovementMethod()
    }
    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MainActivity::class.java)
        findViewById<TextView>(R.id.backtut).setOnClickListener {
            backtut.isClickable = false
            startActivity(intent)
        }
    }
}