package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sign_up.*

@Suppress("DEPRECATION")
class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
    }
    override fun onStart() {
        super.onStart()
        val intent = Intent(this, SigninActivity::class.java)
        findViewById<TextView>(R.id.backsu).setOnClickListener {
            startActivity(intent)
        }
    }

    private fun register (email : String, password : String) : Boolean{
        return true // TODO : дописать функцию
    }

    fun signUp (view: View) {
        val email = mail.text.toString()
        val password = password.text.toString()
        var maskOk = false
        if (email.count{ it == '@' } == 1) {
            val domain = email.substring(email.indexOf('@'))
            if (domain.contains('.')) maskOk = true
        }
        if (maskOk) {
            if (register(email, password)) {
                val intent = Intent(this, SigninActivity::class.java)
                startActivity(intent)
            }
            else Toast.makeText(this, "Declined by server", Toast.LENGTH_SHORT).show()
        }
        else Toast.makeText(this, "Incorrect email!", Toast.LENGTH_SHORT).show()
    }
}