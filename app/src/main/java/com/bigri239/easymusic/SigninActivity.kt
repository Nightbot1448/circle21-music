package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bigri239.easymusic.databinding.ActivitySignInBinding
import com.bigri239.easymusic.net.WebRequester


@Suppress("DEPRECATION")
class SigninActivity : AppCompatActivity() {
    private lateinit var webRequester : WebRequester
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root.also {
            setContentView(it)
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        webRequester = WebRequester(this@SigninActivity)
    }

    override fun onStart() {
        super.onStart()

        val intent = Intent(this, MainActivity::class.java)
        binding.backsi.setOnClickListener {
            binding.backsi.isClickable = false
            startActivity(intent)
        }

        val intents2 = Intent(this, SignupActivity::class.java)
        binding.signup.setOnClickListener {
            binding.signup.isClickable = false
            startActivity(intents2)
        }

        if (webRequester.checkAuthorized()) {
            val intents3 = Intent(this, RecoveryActivity::class.java)
            startActivity(intents3)
        }

    }

    fun logIn (view : View) {
        if (binding.mail.text.toString().trim().isNotEmpty() &&
            binding.password.text.toString().trim().isNotEmpty()) {
            if (webRequester.logIn(binding.mail.text.toString(), binding.password.text.toString())) {
                val intents3 = Intent(this, RecoveryActivity::class.java)
                startActivity(intents3)
            }
            else Toast.makeText(this, "Wrong login or password!", Toast.LENGTH_SHORT)
                .show()
        }
    }

}