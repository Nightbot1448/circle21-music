package com.makentoshe.androidgithubcitemplate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.R
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.media.SoundPool
import android.media.MediaPlayer
import android.os.Environment
import android.os.Environment.*
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private var soundPool: SoundPool? = null
    private var soundId = 1
    private var soundId2 = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.makentoshe.androidgithubcitemplate.R.layout.activity_main)
        setContentView(com.makentoshe.androidgithubcitemplate.R.layout.activity_addfiles)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        soundPool = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
        soundId = soundPool!!.load(baseContext, com.makentoshe.androidgithubcitemplate.R.raw.file1, 0)
        soundId2 = soundPool!!.load(baseContext, com.makentoshe.androidgithubcitemplate.R.raw.file1, 0)
    }
    fun playSound(view: View) { // TODO: заменить onClick у KICK с этого на правильный, когда будет исправлена раскладка
        Toast.makeText(this, "Playing compilated music...", Toast.LENGTH_SHORT).show()
        var ratio = 0.5F // по фану, для демонстрации
        soundPool?.play(soundId, 0.5F, 0.5F, 0, 0, ratio)
        var soundLen : Long = 4 // TODO: заменить на подсчет длины звука
        TimeUnit.SECONDS.sleep((soundLen / ratio).toLong())
        soundPool?.play(soundId2, 1F, 1F, 0, 0, 1F)
        Toast.makeText(this, "Completed!", Toast.LENGTH_SHORT).show()
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
