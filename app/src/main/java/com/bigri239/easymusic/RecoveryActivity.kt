package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_recovery.*

@Suppress("DEPRECATION")
class RecoveryActivity : AppCompatActivity() {
    private var itemsList : List<String> = arrayListOf()
    private var itemsList1 : List<String> = arrayListOf()
    private var itemsList2 : List<String> = arrayListOf()
    private lateinit var webRequester : WebRequester
    private lateinit var customAdapter1: CustomAdapter
    private lateinit var customAdapter: CustomAdapter
    private lateinit var customAdapter2: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovery)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        webRequester = WebRequester(this@RecoveryActivity)

        val info = webRequester.getInfo()
        if (!info.contentEquals(Array (5) {arrayOf("")})) {
            username.text = "Username: " + info[0][0]
            editInfo.setText(info[1][0])
            itemsList = info[2]
            itemsList1 = info[3]
            itemsList2 = info[4]

        }
        else {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        customAdapter = CustomAdapter(itemsList)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = customAdapter

        val recyclerView1: RecyclerView = findViewById(R.id.recyclerView2)
        customAdapter1 = CustomAdapter(itemsList1)
        val layoutManager1 = LinearLayoutManager(applicationContext)
        recyclerView1.layoutManager = layoutManager1
        recyclerView1.adapter = customAdapter1

        val recyclerView2: RecyclerView = findViewById(R.id.recyclerView3)
        customAdapter2 = CustomAdapter(itemsList2)
        val layoutManager2 = LinearLayoutManager(applicationContext)
        recyclerView2.layoutManager = layoutManager2
        recyclerView2.adapter = customAdapter2

    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MainActivity::class.java)
        findViewById<TextView>(R.id.backrec).setOnClickListener {
            startActivity(intent)
        }
    }

    fun logOff (view: View) {
        if (webRequester.logOff()) {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }
    }
}