package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

@Suppress("DEPRECATION")
class SigninActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
    }
    override fun onStart() {
        super.onStart()
        val intent = Intent(this, RecoveryActivity::class.java)
        findViewById<TextView>(R.id.backsi).setOnClickListener {
            startActivity(intent)
        }
        val intents2 = Intent(this, SignupActivity::class.java)
        findViewById<TextView>(R.id.signup).setOnClickListener {
            startActivity(intents2)
        }
    }

}