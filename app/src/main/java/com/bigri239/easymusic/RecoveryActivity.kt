package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

@Suppress("DEPRECATION")
class RecoveryActivity : AppCompatActivity() {
    private val itemsList = ArrayList<String>()
    private lateinit var customAdapter: CustomAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovery)


        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        customAdapter = CustomAdapter(itemsList)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = customAdapter
        prepareItems()
    }
    private fun prepareItems() {
        itemsList.add("User 1")
        itemsList.add("User 2")
        itemsList.add("User 3")
        itemsList.add("User 4")
        itemsList.add("User5")
        itemsList.add("User 6")
        customAdapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MainActivity::class.java)
        findViewById<TextView>(R.id.backrec).setOnClickListener {
            startActivity(intent)
        }
        val intents1 = Intent(this, SigninActivity::class.java)
        findViewById<TextView>(R.id.signin).setOnClickListener {
            startActivity(intents1)
        }

    }
}