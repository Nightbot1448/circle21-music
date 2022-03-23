package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class AddingfilesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addfiles)
        supportActionBar?.hide()
    }
    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MainActivity::class.java)
        findViewById<TextView>(R.id.back_A).setOnClickListener {
            startActivity(intent)
        }
    }
    fun openFile(view: View) {
        Toast.makeText(this, "Opening file...", Toast.LENGTH_SHORT).show()
        val path = getFilesDir()
        val file = File(path, "file.txt")
        FileOutputStream(file).use {
            it.write("record goes here".toByteArray())
        }
        val contents = file.readText()
        Toast.makeText(this, contents, Toast.LENGTH_SHORT).show()
    }
}