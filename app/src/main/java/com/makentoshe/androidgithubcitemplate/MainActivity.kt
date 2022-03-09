package com.makentoshe.androidgithubcitemplate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.media.SoundPool
import android.media.MediaPlayer
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import android.view.ContextMenu.ContextMenuInfo

import android.view.ContextMenu
import android.widget.TextView
import androidx.navigation.findNavController

import android.widget.ProgressBar
import android.os.Handler

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private var soundPool: SoundPool? = null
    private var soundId = 1
    private var soundId2 = 1
    private var soundId3 = 1
    private var soundId4 = 1
    private var menu1 = 1
    private var menu2 = 2
    private var menu3 = 3
    private var progressBar: ProgressBar? = null
    private var i = 0
    private var txtView: TextView? = null
    private val handler = Handler()
    //опишем создание контекстных меню
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        soundPool = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
        soundId = soundPool!!.load(baseContext, com.makentoshe.androidgithubcitemplate.R.raw.file1, 0)
        soundId2 = soundPool!!.load(baseContext, com.makentoshe.androidgithubcitemplate.R.raw.file1, 0)
        soundId3 = soundPool!!.load(baseContext, com.makentoshe.androidgithubcitemplate.R.raw.file2, 0)
        soundId4 = soundPool!!.load(baseContext, com.makentoshe.androidgithubcitemplate.R.raw.file2, 0)

        var firstsound = findViewById<Button>(R.id.KICK)

        //создание контекстного меню
        registerForContextMenu(firstsound);
    }
// ниже создание переходов между экранами
    override fun onStart() {
        super.onStart()
        val intent = Intent(this, HelpActivity::class.java)
        findViewById<TextView>(R.id.help).setOnClickListener {
            startActivity(intent)
        }
        val intent1 = Intent(this, AddingfilesActivity::class.java)
        findViewById<TextView>(R.id.file).setOnClickListener {
            startActivity(intent1)
            }
    }

    //findNavController(R.id.app_graph).navigate(R.id.action_mainActivity_to_helpActivity)
    override fun onCreateContextMenu(
        menu: ContextMenu, v: View,
        menuInfo: ContextMenuInfo?
    ) {
        // TODO Auto-generated method stub
        when (v.id) {
            R.id.KICK -> {
                menu.add(0, menu1, 0, "kick")
                menu.add(0, menu2, 0, "snare")
                menu.add(0, menu3, 0, "hihat")
            }
        }
    }
    override fun onContextItemSelected(item: menu1?): Boolean {
        return when (item!!.itemId) {
            R.id.menu1->{
                Toast.makeText(applicationContext, "call code", Toast.LENGTH_LONG).show()
                return true
            }
            R.id.menu2->{
                Toast.makeText(applicationContext, "sms code", Toast.LENGTH_LONG).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        soundPool = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
        soundId = soundPool!!.load(baseContext, R.raw.file1, 0)
        soundId2 = soundPool!!.load(baseContext, R.raw.file1, 0)
    }
    fun playSound(view: View) { // TODO: заменить onClick у KICK с этого на правильный, когда будет исправлена раскладка
        Toast.makeText(this, "Playing compiled music...", Toast.LENGTH_SHORT).show()
        var ratio = 0.5F // по фану, для демонстрации
        soundPool?.play(soundId, 0.5F, 0.5F, 0, 0, ratio)
        var soundLen : Long = 4 // TODO: заменить на подсчет длины звука
        TimeUnit.SECONDS.sleep((soundLen / ratio).toLong())
        soundPool?.play(soundId2, 1F, 1F, 0, 0, 1F)
        Toast.makeText(this, "Completed!", Toast.LENGTH_SHORT).show()
    }
}