package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import kotlinx.android.synthetic.main.settings_activity.*
import java.io.File
import java.io.FileOutputStream

@Suppress("DEPRECATION")
class SettingsActivity : AppCompatActivity() {
    private var autosave = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        val file = File(filesDir, "settings.conf")

        if (file.exists()) {
            val content: String = file.readText()
            if (content != "") autosave = content.split("\n").toTypedArray()[0].toInt()
        }
        else {
            val content = "10"
            FileOutputStream(file).write(content.toByteArray())
        }

        textMins.text = autosave.toString()
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MainActivity::class.java)
        backsettings.setOnClickListener {
            backsettings.isClickable = false
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

    private fun autosaveTimePopupMenuClickListener(menuItem: MenuItem) {
        val itemTitle = menuItem.title.toString()
        autosave = itemTitle.toInt()
        textMins.text = itemTitle
        val file = File(filesDir, "settings.conf")
        val content = itemTitle
        FileOutputStream(file).write(content.toByteArray())
    }

    fun createAutosaveTimePopupMenu(v: View) {
        val variants = arrayOf(1, 2, 5, 10, 15, 20, 30, 45, 60)
        val popupMenu = PopupMenu(this, v)
        for (i in variants) popupMenu.menu.add(i.toString())
        popupMenu.setOnMenuItemClickListener { autosaveTimePopupMenuClickListener(it); true }
        popupMenu.show()
    }
}