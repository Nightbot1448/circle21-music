package com.bigri239.easymusic

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.*
import androidx.recyclerview.widget.DefaultItemAnimator


@Suppress("DEPRECATION")
class AddingfilesActivity : AppCompatActivity() {
    private var filePath = ""
    private val itemsList = ArrayList<String>()
    private val itemsList1 = ArrayList<String>()
    private lateinit var customAdapter: CustomAdapter
    private lateinit var customAdapter1: CustomAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addfiles)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView111)
        customAdapter = CustomAdapter(itemsList)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = customAdapter
        prepareItems()

        val recyclerView1: RecyclerView = findViewById(R.id.recyclerView222)
        customAdapter1 = CustomAdapter(itemsList1)
        val layoutManager1 = LinearLayoutManager(applicationContext)
        recyclerView1.layoutManager = layoutManager1
        recyclerView1.adapter = customAdapter
        prepareItems1()
    }
    private fun prepareItems() {
        itemsList.add("Sound 1")
        itemsList.add("Sound 2")
        itemsList.add("Sound 3")
        itemsList.add("Sound 4")
        itemsList.add("Sound 5")
        itemsList.add("Sound 6")
        customAdapter.notifyDataSetChanged()


        supportActionBar?.hide()
    }
    private fun prepareItems1() {
        itemsList1.add("Sound aa")
        itemsList1.add("Sound bb")
        itemsList1.add("Sound cc")
        itemsList1.add("Sound dd")
        itemsList1.add("Sound sw")
        itemsList1.add("Sound egdeiugd")
        customAdapter1.notifyDataSetChanged()


        supportActionBar?.hide()
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MainActivity::class.java)
        findViewById<TextView>(R.id.back_A).setOnClickListener {
            startActivity(intent)
        }
    }

    @Throws(IOException::class)
    fun readBytes(inputStream: InputStream): ByteArray? {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len: Int
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }

    private fun showCopyingDialog(content :ByteArray) {
        val dialog = Dialog(this, R.style.ThemeOverlay_Material3_Dialog)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_sound_name)
        dialog.findViewById<Button>(R.id.save).setOnClickListener {
            filePath = dialog.findViewById<EditText>(R.id.newname).text.toString()
            val path = filesDir
            val file = File(path, "$filePath.wav")
            FileOutputStream(file).use {
                it.write(content)
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 777) {
            val input: InputStream? = data!!.data?.let { contentResolver.openInputStream(it) }
            val wavdata = input?.let { readBytes(it) }
            if (wavdata != null) {
                showCopyingDialog(wavdata)
            }
        }
    }

    fun openFile(view: View) {
        Toast.makeText(this, "Opening file...", Toast.LENGTH_SHORT).show()
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select a file"), 777)
    }
}