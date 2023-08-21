package com.bigri239.easymusic

import android.app.Dialog
import android.content.Intent
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.SoundPool
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bigri239.easymusic.adapter.CustomAdapter
import com.bigri239.easymusic.adapter.CustomConnector
import com.bigri239.easymusic.databinding.ActivityAddfilesBinding
import java.io.*
import kotlin.math.abs


@Suppress("DEPRECATION")
class AddingfilesActivity : AppCompatActivity() {
    private var filePath = ""
    private var id = 0
    private var len : Long = 3615
    private var track = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
    private val defaultList : ArrayList<String> = arrayListOf("aaf", "ablockboy", "ashawty",
        "asnake", "aspirit", "bassalbane", "basscentury", "bassflowers", "clapaf", "clapchoppa",
        "clapcrazy", "clapflip", "clapforeign", "clapjuice", "clapple", "claprev", "clapslime",
        "clapsoda", "clapspace", "crashalect", "crashbloods", "crashvinnyx", "cymbalaf",
        "cymbalblockboy", "cymbalblueface", "cymbalglasses", "cymbalhoodfight", "cymbalpancake",
        "fxcillbill", "fxcup", "fxfreeze", "fxguncock", "fxgunnes", "hhbigmoney", "hhgang",
        "hhgotit", "hhhood", "hhple", "hhpunch", "hhshawty", "hhsnake", "hhsoft", "hhspace",
        "hihatcheque", "hihatmystery", "kickaf", "kickartillery", "kickflip", "kickhood",
        "kickinfinite", "kickjordan", "kickpunch", "kickslap", "ohaf", "ohbandana",
        "ohblockboy", "ohkiss", "ohlow", "ohog", "ohstick", "ohwork", "percaf", "percardonme",
        "percblockboy", "percgame", "percgoofy", "percicy", "perclame", "percnotavalible",
        "percoldchair", "percpaolla", "percpegas", "percple", "percroll", "percrun", "percset",
        "percslime", "percwoodtoy", "rimchaser", "rimstount", "snareblockboy", "snarechop",
        "snarecompas", "snarehood", "snareshawty", "snareslime", "snaretango", "snarewoods",
        "voxanother", "voxgilens")
    private val customList = arrayListOf<String>()
    private lateinit var defaultAdapter: CustomAdapter
    private lateinit var customAdapter: CustomAdapter
    private lateinit var binding: ActivityAddfilesBinding

    private val connectorSound = object : CustomConnector {
        override fun function(string: String) {
            playSound(string)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddfilesBinding.inflate(layoutInflater)
        val view = binding.root.also {
            setContentView(it)
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        val path = filesDir
        val file = File(path, "sounds.conf")

        if (file.exists()) {
            val content: String = file.readText()
            if (content != "") customList.addAll(content.split("\n").toTypedArray())
        }
        else FileOutputStream(file).write("".toByteArray())

        val recyclerView: RecyclerView = binding.recyclerView111
        defaultAdapter = CustomAdapter(defaultList, connectorSound)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = defaultAdapter
        recyclerView.layoutParams = getLayoutParametersRelativeWidth()

        val recyclerView1: RecyclerView = binding.recyclerView222
        customAdapter = CustomAdapter(customList, connectorSound)
        recyclerView1.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView1.adapter = customAdapter
        recyclerView1.layoutParams = getLayoutParametersRelativeWidth()
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MainActivity::class.java)
        findViewById<TextView>(R.id.back_A).setOnClickListener {
            binding.backA.isClickable = false
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
            if (filePath != "" && !filePath.contains(';')) {
                val path = filesDir
                val file = File(path, "$filePath.wav")
                FileOutputStream(file).write(content)
                val file1 = File(path, "sounds.conf")
                var sounds: String = file1.readText()
                if (!customList.contains(filePath)) {
                    if (sounds != "") sounds += "\n"
                    sounds += filePath
                    FileOutputStream(file1).write(sounds.toByteArray())
                    customList.add(filePath)
                    customAdapter.notifyItemInserted(customList.size - 1)
                }
                dialog.dismiss()
                Toast.makeText(this, "Sound added successfully!", Toast.LENGTH_SHORT)
                    .show()
            }
            else Toast.makeText(this, "Incorrect file name!", Toast.LENGTH_SHORT).show()
        }
        dialog.show()
    }

    private fun getLayoutParametersRelativeWidth (): LinearLayout.LayoutParams {
        val scale: Float = resources.displayMetrics.density
        val displayMetrics = resources.displayMetrics
        val pixelsWidth = (displayMetrics.widthPixels * 0.425F).toInt()
        val pixelsHeight = (200 * scale + 0.5f).toInt()
        return LinearLayout.LayoutParams(pixelsWidth, pixelsHeight)
    }

    private fun rawResourceToSound (resourceName : String) {
        val res = resources
        val inStream: InputStream = res.openRawResource(res.getIdentifier(resourceName,
            "raw", packageName))
        val data = readBytes(inStream)
        val firstProject = File(filesDir, "sound.wav")
        FileOutputStream(firstProject).write(data)
    }

    private fun getSoundLength(name: String): Long {
        return try {
            val metaRetriever = MediaMetadataRetriever()
            if (defaultList.contains(name)) {
                rawResourceToSound(name)
                metaRetriever.setDataSource(File(filesDir, "sound.wav").absolutePath)
            }
            else metaRetriever.setDataSource(File(filesDir, "$name.wav").absolutePath)
            abs(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.
                toLong() - 1)
        }
        catch (e: Exception) {
            0
        }
    }

    private fun playSound (soundName : String) {
        len = getSoundLength(soundName)
        if (len != 0.toLong()) {
            binding.name.text = "Sound name: $soundName"
            binding.length.text = "Sound length: $len"
            track.release()
            track = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
            id = if (defaultList.contains(soundName)) track.load(
                baseContext,
                resources.getIdentifier(soundName, "raw", packageName),
                0
            )
            else track.load("$filesDir/$soundName.wav",0)
            play()
        }
        else Toast.makeText(this, "Oops! This sound does not exist!",
            Toast.LENGTH_SHORT).show()
    }

    private fun play() {
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
                val file = File(filesDir, "sound.wav")
                FileOutputStream(file).write(wavdata)
                val len = getSoundLength("sound.wav")
                if (len < 5600) {
                    showCopyingDialog(wavdata)
                }
                else Toast.makeText(this, "Oops! This file is too long!",
                    Toast.LENGTH_SHORT).show()
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