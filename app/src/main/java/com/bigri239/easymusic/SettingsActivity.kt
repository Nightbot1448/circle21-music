package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.settings_activity.*
import java.io.File
import java.lang.Exception

@Suppress("DEPRECATION")
class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
    }
    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MainActivity::class.java)
        findViewById<TextView>(R.id.backsettings).setOnClickListener {
            startActivity(intent)
        }
    }

    fun reset (view : View) {
        val deleteSounds = checkBox.isChecked
        val deleteProjects = checkBox2.isChecked
        val path = filesDir
        if (deleteSounds) {
            val file = File(path, "sounds.conf")
            if (file.exists()) {
                val content: String = file.readText()
                val sounds = content.split("\n").toTypedArray()
                for (i in sounds) {
                    val sound = File(path, "$i.wav")
                    if (sound.exists()) sound.delete()
                }
                file.delete()
            }
        }
        if (deleteProjects) {
            val file = File(path, "projects.conf")
            if (file.exists()) {
                val content: String = file.readText()
                val projects = content.split("\n").toTypedArray()
                val projectDefault = File(path, "projectDefault.emproj")
                if (projectDefault.exists()) projectDefault.delete()
                for (i in projects) {
                    val project = File(path, i)
                    if (project.exists()) project.delete()
                }
                file.delete()
            }
        }
    }
}