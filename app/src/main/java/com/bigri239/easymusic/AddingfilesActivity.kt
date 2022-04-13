package com.bigri239.easymusic

import android.app.Dialog
import android.content.Intent
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_addfiles.*
import java.io.*
import kotlin.math.abs


@Suppress("DEPRECATION")
class AddingfilesActivity : AppCompatActivity() {
    private var filePath = ""
    private var isPlaying = false
    private var id = 0
    private var len : Long = 3615
    var track = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
    private val itemsList : ArrayList<String> = arrayListOf("bassalbane", "basscentury", "bassflowers",
        "clapchoppa", "clapforeign", "crashalect", "crashbloods", "crashvinnyx", "fxfreeze",
        "fxgunnes", "hihatcheque", "hihatmystery", "kickartillery", "kickinfinite", "percardonme",
        "percpaolla", "rimchaser", "rimstount", "snarecompas", "snarewoods", "voxanother", "voxgilens")
    private val itemsList1 = arrayListOf<String>()
    private lateinit var customAdapter: CustomAdapter
    private lateinit var customAdapter1: CustomAdapter
    private val connectorSound = object : RecoveryActivity.WebConnector {
        override fun function(string: String) {
            playSound(string)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addfiles)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        val path = filesDir
        val file = File(path, "sounds.conf")

        if (file.exists()) {
            val content: String = file.readText()
            if (content != "") itemsList1.addAll(content.split("\n").toTypedArray())
        }
        else {
            FileOutputStream(file).use {
                it.write("".toByteArray())
            }
        }

        val recyclerView: RecyclerView = recyclerView111
        customAdapter = CustomAdapter(itemsList, connectorSound)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = customAdapter

        val recyclerView1: RecyclerView = recyclerView222
        customAdapter1 = CustomAdapter(itemsList1, connectorSound)
        val layoutManager1 = LinearLayoutManager(applicationContext)
        recyclerView1.layoutManager = layoutManager1
        recyclerView1.adapter = customAdapter1
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MainActivity::class.java)
        findViewById<TextView>(R.id.back_A).setOnClickListener {
            startActivity(intent)
            back_A.setOnClickListener {}
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
            if (filePath != "") {
                val path = filesDir
                val file = File(path, "$filePath.wav")
                FileOutputStream(file).use {
                    it.write(content)
                }
                val file1 = File(path, "sounds.conf")
                var sounds: String = file1.readText()
                if (sounds != "") sounds += "\n"
                sounds += filePath
                FileOutputStream(file1).use {
                    it.write(sounds.toByteArray())
                }
                if (!itemsList1.contains(filePath)) itemsList1.add(filePath)
                customAdapter1.notifyDataSetChanged()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun getSoundLength(name: String): Long {
        try {
            val metaRetriever = MediaMetadataRetriever()
            if (itemsList.contains(name)) {
                val inStream: InputStream = resources.openRawResource(resources.getIdentifier(name, "raw", packageName))
                val data = readBytes(inStream)
                val file = File(filesDir, "sound.wav")
                FileOutputStream(file).use {
                    it.write(data)
                }
                metaRetriever.setDataSource(File(filesDir, "sound.wav").absolutePath)
            }
            else metaRetriever.setDataSource(File(filesDir, "$name.wav").absolutePath)
            return abs(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toLong() - 1)
        }
        catch (e: java.lang.Exception) {
            return 0
        }
    }

    private fun playSound (soundName : String) {
        len = getSoundLength(soundName)
        if (len != 0.toLong()) {
            name.text = "Sound name: $soundName"
            length.text = "Sound length: $len"
            if (!isPlaying) {
                isPlaying = true
                track = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
                id = if (itemsList.contains(soundName)) track.load(
                    baseContext,
                    resources.getIdentifier(soundName, "raw", packageName),
                    0
                )
                else track.load("$filesDir/$soundName.wav",0)
                play()
                object : CountDownTimer(len, 1000) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        track.release()
                        isPlaying = false
                    }
                }.start()
           }
        }
        else Toast.makeText(this, "Oops! This sound does not exist!", Toast.LENGTH_SHORT).show()
    }

    fun play() {
        track.setOnLoadCompleteListener { _, _, _ ->
            track.play(id, 1.0F, 1.0F, 0, 0, 1.0F)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == 777) {
            val input: InputStream? = data!!.data?.let { contentResolver.openInputStream(it) }
            val wavdata = input?.let { readBytes(it) }
            if (wavdata != null) {
                showCopyingDialog(wavdata)
            }
        }
        }
        catch (e: Exception) {
            Toast.makeText(this, "Nothing selected!", Toast.LENGTH_SHORT).show()
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