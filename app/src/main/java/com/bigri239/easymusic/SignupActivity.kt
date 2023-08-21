package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bigri239.easymusic.databinding.ActivitySignUpBinding
import com.bigri239.easymusic.net.WebRequester

@Suppress("DEPRECATION")
class SignupActivity : AppCompatActivity() {
    private lateinit var webRequester : WebRequester
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root.also {
            setContentView(it)
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        webRequester = WebRequester(this@SignupActivity)
    }

    override fun onStart() {
        super.onStart()

        val intent = Intent(this, SigninActivity::class.java)
        binding.backsu.setOnClickListener {
            binding.backsu.isClickable = false
            startActivity(intent)
        }
    }

    fun signUp (view: View) {
        if (binding.mail.text.toString().trim().isNotEmpty() &&
            binding.password.text.toString().trim().isNotEmpty()) {
            val email = binding.mail.text.toString()
            val password = binding.password.text.toString()
            var maskOk = false
            if (email.count{ it == '@' } == 1) {
                val domain = email.substring(email.indexOf('@'))
                if (domain.contains('.')) maskOk = true
            }
            if (maskOk) {
                if (webRequester.signUp(email, password)) {
                    Toast.makeText(this, "Check your email for an approval link",
                        Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, SigninActivity::class.java)
                    startActivity(intent)
                }
                else Toast.makeText(this, "This account already exists",
                    Toast.LENGTH_SHORT).show()
            }
            else Toast.makeText(this, "Incorrect email!", Toast.LENGTH_SHORT).show()
        }
    }
}