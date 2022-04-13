package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.android.synthetic.main.activity_sign_in.*


@Suppress("DEPRECATION")
class SigninActivity : AppCompatActivity() {
    private lateinit var webRequester : WebRequester

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        webRequester = WebRequester(this@SigninActivity)
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MainActivity::class.java)
        findViewById<TextView>(R.id.backsi).setOnClickListener {
            backsi.isClickable = false
            startActivity(intent)
        }
        val intents2 = Intent(this, SignupActivity::class.java)
        findViewById<TextView>(R.id.signup).setOnClickListener {
            signup.isClickable = false
            startActivity(intents2)
        }
        if (webRequester.checkAuthorized()) {
            val intents3 = Intent(this, RecoveryActivity::class.java)
            startActivity(intents3)
        }

    }

    fun logIn (view : View) {
        if (mail.text.toString().trim().isNotEmpty() && password.text.toString().trim().isNotEmpty()) {
            if (webRequester.logIn(mail.text.toString(), password.text.toString())) {
                val intents3 = Intent(this, RecoveryActivity::class.java)
                startActivity(intents3)
            }
            else Toast.makeText(this, "Wrong login or password!", Toast.LENGTH_SHORT).show()
        }
    }

}