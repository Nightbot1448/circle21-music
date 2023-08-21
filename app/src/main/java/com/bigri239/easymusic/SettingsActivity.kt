package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import com.bigri239.easymusic.databinding.SettingsActivityBinding
import java.io.File
import java.io.FileOutputStream

@Suppress("DEPRECATION")
class SettingsActivity : AppCompatActivity() {
    private var autosave = 10
    private var updateType = 2
    private lateinit var binding: SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        val view = binding.root.also {
            setContentView(it)
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        val file = File(filesDir, "settings.conf")
        val defaultSettings = "10\n3\nprojectDefault"
        val defaultNs = defaultSettings.count {it == '\n'}

        if (!file.exists()) {
            FileOutputStream(file).write(defaultSettings.toByteArray())
        }

        var content: String = file.readText()

        if (content.count {it == '\n'} != defaultNs) {
            val missingStrings = defaultNs - content.count {it == '\n'}
            content += "\n" + defaultSettings.split("\n").toTypedArray().slice(
                (defaultNs - missingStrings + 1)..defaultNs).joinToString("\n")
            FileOutputStream(file).write(content.toByteArray())
        }

        val contentArray = content.split("\n").toTypedArray()
        autosave = contentArray[0].toInt()
        updateType = contentArray[1].toInt()

        binding.textMins.text = autosave.toString()
        if (updateType / 4 == 1) binding.checkBoxAlpha.isChecked = true
        if ((updateType % 4) / 2 == 1) binding.checkBoxBeta.isChecked = true
        if (updateType % 2 == 1) binding.checkBoxStable.isChecked = true
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MainActivity::class.java)
        binding.backsettings.setOnClickListener {
            binding.backsettings.isClickable = false
            startActivity(intent)
        }
    }

    fun reset (view : View) {
        val deleteSounds = binding.checkBox.isChecked
        val deleteProjects = binding.checkBox2.isChecked
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
                Toast.makeText(this, "All custom sounds deleted successfully",
                    Toast.LENGTH_LONG).show()
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
                    val project = File(path, "$i.emproj")
                    if (project.exists()) project.delete()
                }

                file.delete()
                writeChanges(2, "projectDefault")
                Toast.makeText(this, "All projects deleted successfully",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    fun saveNotify (view: View) {
        updateType = 0
        if (binding.checkBoxAlpha.isChecked) updateType += 4
        if (binding.checkBoxBeta.isChecked) updateType += 2
        if (binding.checkBoxStable.isChecked) updateType += 1
        writeChanges(1, updateType.toString())
        Toast.makeText(this, "Notification mode changed successfully",
            Toast.LENGTH_LONG).show()
    }

    private fun writeChanges (settingNumber : Int, settingString : String) {
        val file = File(filesDir, "settings.conf")
        val content: String = file.readText()
        val contentArray = content.split("\n").toTypedArray()
        contentArray[settingNumber] = settingString
        FileOutputStream(file).write(contentArray.joinToString("\n").toByteArray())
    }

    private fun autosaveTimePopupMenuClickListener(menuItem: MenuItem) {
        val itemTitle = menuItem.title.toString()
        autosave = itemTitle.toInt()
        binding.textMins.text = itemTitle
        writeChanges(0, itemTitle)
        Toast.makeText(this, "Autosave interval changed successfully",
            Toast.LENGTH_LONG).show()
    }

    fun createAutosaveTimePopupMenu(v: View) {
        val variants = arrayOf(1, 2, 5, 10, 15, 20, 30, 45, 60)
        val popupMenu = PopupMenu(this, v)
        for (i in variants) popupMenu.menu.add(i.toString())
        popupMenu.setOnMenuItemClickListener { autosaveTimePopupMenuClickListener(it); true }
        popupMenu.show()
    }
}