package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigri239.easymusic.adapter.*
import com.bigri239.easymusic.net.WebRequester
import kotlinx.android.synthetic.main.activity_friend.*

@Suppress("DEPRECATION")
class FriendActivity : AppCompatActivity() {

    private lateinit var email : String
    private var itemsList1 : List<String> = arrayListOf()
    private var itemsList2 : List<String> = arrayListOf()
    private lateinit var webRequester : WebRequester
    private lateinit var customAdapter1: CustomAdapter
    private lateinit var customAdapter2: CustomAdapter
    private val connectorProject = object : CustomConnector {
        override fun function(string: String) {
            loadProject(string)
        }
    }

    private val connectorSound = object : CustomConnector {
        override fun function(string: String) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend)
        email = intent.getStringExtra("owner").toString()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        webRequester = WebRequester(this@FriendActivity)
    }

    override fun onStart() {
        super.onStart()
        val info = email.let { webRequester.getFriendInfo(it) }
        if (!info.contentEquals(Array (5) {arrayOf("")})) {
            username.text = "Username: " + info[0][0]
            aboutme.text = "About me: " + info[1][0]
            itemsList1 = info[3]
            itemsList2 = info[4]
        }
        else {
            val intent = Intent(this, RecoveryActivity::class.java)
            startActivity(intent)
        }

        val intent = Intent(this, RecoveryActivity::class.java)
        backrec.setOnClickListener {
            backrec.isClickable = false
            startActivity(intent)
        }

        customAdapter1 = if (itemsList1 != arrayListOf("")) CustomAdapter(itemsList1,
            connectorSound)
        else CustomAdapter(arrayListOf(), connectorSound)
        val layoutManager1 = LinearLayoutManager(applicationContext)
        recyclerView2.layoutManager = layoutManager1
        recyclerView2.adapter = customAdapter1

        customAdapter2 = if (itemsList2 != arrayListOf("")) CustomAdapter(itemsList2,
            connectorProject)
        else CustomAdapter(arrayListOf(), connectorProject)
        val layoutManager2 = LinearLayoutManager(applicationContext)
        recyclerView3.layoutManager = layoutManager2
        recyclerView3.adapter = customAdapter2
    }

    private fun loadProject(string: String) {
        if (webRequester.getProject(email, string)) Toast.makeText(this,
            "Project $string downloaded", Toast.LENGTH_SHORT).show()
        else Toast.makeText(this, "Oops! Something went wrong!",
            Toast.LENGTH_SHORT).show()
    }
}