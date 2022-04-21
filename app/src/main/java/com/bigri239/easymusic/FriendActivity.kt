package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.LinearLayout
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
    private var soundsList : List<String> = arrayListOf()
    private var projectsList : List<String> = arrayListOf()
    private lateinit var webRequester : WebRequester
    private lateinit var soundsAdapter: CustomAdapter
    private lateinit var projectsAdapter: CustomAdapter
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
            soundsList = info[3]
            projectsList = info[4]
            if (soundsList == arrayListOf("")) soundsList = arrayListOf()
            if (projectsList == arrayListOf("")) projectsList = arrayListOf()
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

        soundsAdapter = CustomAdapter(soundsList, connectorSound)
        recyclerView2.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView2.adapter = soundsAdapter
        recyclerView2.layoutParams = getLayoutParametersRelativeWidth()

        projectsAdapter = CustomAdapter(projectsList, connectorProject)
        recyclerView3.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView3.adapter = projectsAdapter
        recyclerView3.layoutParams = getLayoutParametersRelativeWidth()
    }

    private fun getLayoutParametersRelativeWidth (): LinearLayout.LayoutParams {
        val scale: Float = resources.displayMetrics.density
        val displayMetrics = resources.displayMetrics
        val pixelsWidth = (displayMetrics.widthPixels * 0.45F).toInt()
        val pixelsHeight = (200 * scale + 0.5f).toInt()
        return LinearLayout.LayoutParams(pixelsWidth, pixelsHeight)
    }

    private fun loadProject(string: String) {
        if (webRequester.getProject(email, string)) Toast.makeText(this,
            "Project $string downloaded", Toast.LENGTH_SHORT).show()
        else Toast.makeText(this, "Oops! Something went wrong!",
            Toast.LENGTH_SHORT).show()
    }
}