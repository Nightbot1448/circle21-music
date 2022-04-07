package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

@Suppress("DEPRECATION")
class RecoveryActivity : AppCompatActivity() {
    private val itemsList : ArrayList<String> = arrayListOf(
        "User 1",
        "User 2",
        "User 3",
        "User 4",
        "User 5",
        "User 6",
        "User 7",
        "User 8",
)
    private val itemsList1 : ArrayList<String> = arrayListOf(
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        )
    private val itemsList2 : ArrayList<String> = arrayListOf(
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
    )
    private lateinit var customAdapter1: CustomAdapter
    private lateinit var customAdapter: CustomAdapter
    private lateinit var customAdapter2: CustomAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovery)


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
}