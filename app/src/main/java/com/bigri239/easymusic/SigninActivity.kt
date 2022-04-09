package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sign_in.*


@Suppress("DEPRECATION")
class SigninActivity : AppCompatActivity() {
    //private val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    //private val arePermissionsGranted : Array<Boolean> = Array(permissions.size) {false}
    private lateinit var webRequester : WebRequester

    /*private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean ->
            if (!isGranted) {
                failedRequestAction()
            }
        }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
    }
    override fun onStart() {
        super.onStart()
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        webRequester = WebRequester(this@SigninActivity)
        val intent = Intent(this, MainActivity::class.java)
        findViewById<TextView>(R.id.backsi).setOnClickListener {
            startActivity(intent)
        }
        val intents2 = Intent(this, SignupActivity::class.java)
        findViewById<TextView>(R.id.signup).setOnClickListener {
            startActivity(intents2)
        }

        if (webRequester.checkAuthorized()) {
            val intents3 = Intent(this, RecoveryActivity::class.java)
            startActivity(intents3)
        }

    }

    /*private fun checkPermissions () {
        for (i in permissions.indices) {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(baseContext, permissions[i]) -> {
                    arePermissionsGranted[i] = true
                }
                else -> {
                    requestPermissionLauncher.launch(permissions[i])
                    arePermissionsGranted[i] = ContextCompat.checkSelfPermission(baseContext, permissions[i]) == PackageManager.PERMISSION_GRANTED
                }
            }
        }
    }*/

    /*private fun failedRequestAction() {
        //checkPermissions()
        Toast.makeText(this, "Oops! Please give all required permissions!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }*/

    private fun isLogged() : Boolean {
        //checkPermissions()
        return true // TODO : написать функцию
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