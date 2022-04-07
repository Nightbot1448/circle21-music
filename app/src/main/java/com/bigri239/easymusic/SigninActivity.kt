package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

@Suppress("DEPRECATION")
class SigninActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
    }
    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MainActivity::class.java)
        findViewById<TextView>(R.id.backsi).setOnClickListener {
            startActivity(intent)
        }
        val intents2 = Intent(this, SignupActivity::class.java)
        findViewById<TextView>(R.id.signup).setOnClickListener {
            startActivity(intents2)
        }
        if (false/*isLogged()*/) { // TODO : заменить после того, как напишу isLogged()
            val intents3 = Intent(this, RecoveryActivity::class.java)
            startActivity(intents3)
        }
    }

    private fun isLogged() : Boolean {
        return true // TODO : написать функцию
    }

    fun logIn (view : View) {
        if (isLogged()) {
            val intents3 = Intent(this, RecoveryActivity::class.java)
            startActivity(intents3)
        }
        else Toast.makeText(this, "Wrong login or password!", Toast.LENGTH_SHORT).show()
    }

}