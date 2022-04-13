package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.mail
import kotlinx.android.synthetic.main.activity_sign_up.password

@Suppress("DEPRECATION")
class SignupActivity : AppCompatActivity() {
    private lateinit var webRequester : WebRequester

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        webRequester = WebRequester(this@SignupActivity)
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, SigninActivity::class.java)
        findViewById<TextView>(R.id.backsu).setOnClickListener {
            backsu.isClickable = false
            startActivity(intent)
        }
    }

    fun signUp (view: View) {
        if (mail.text.toString().trim().isNotEmpty() && password.text.toString().trim().isNotEmpty()) {
            val email = mail.text.toString()
            val password = password.text.toString()
            var maskOk = false
            if (email.count{ it == '@' } == 1) {
                val domain = email.substring(email.indexOf('@'))
                if (domain.contains('.')) maskOk = true
            }
            if (maskOk) {
                if (webRequester.signUp(email, password)) {
                    Toast.makeText(this, "Check your email for an approval link", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, SigninActivity::class.java)
                    startActivity(intent)
                }
                else Toast.makeText(this, "This account already exists", Toast.LENGTH_SHORT).show()
            }
            else Toast.makeText(this, "Incorrect email!", Toast.LENGTH_SHORT).show()
        }
    }
}