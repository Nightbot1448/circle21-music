package com.makentoshe.androidgithubcitemplate

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.media.AudioManager
import android.media.SoundPool
import android.os.*
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.lang.System.currentTimeMillis
import java.util.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    data class SoundInfo (
        var res: Int = 0,
        var id: Int = 0,
        var delay: Long = 0,
        var volume: Float = 0.0F,
        var loop: Int = 0,
        var ratio: Float = 1.0F
    )
    private var state : String = "unready"
    private val tracks: Array<SoundPool> = Array (100) { SoundPool(10, AudioManager.STREAM_MUSIC, 0) }
    private var countTracks = 0
    private val countSounds: Array<Int> = Array (100) {0}
    private val sounds: Array<Array<SoundInfo>> = Array (100) { Array(1000) {i -> SoundInfo(0, i + 1, 0, 1.0F, 0, 1.0F) } }
    private var progressBar: ProgressBar? = null
    private var i = 0
    private var txtView: TextView? = null
    private lateinit var textView: TextView

    companion object {
        const val IDM_OPEN = 101
        const val IDM_SAVE = 102
    }

    //опишем создание контекстных меню
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        //var firstsound = findViewById<Button>(R.id.KICK)

        textView = findViewById(R.id.txt)
        registerForContextMenu(textView)
        //создание контекстного меню

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
        val intent2 = Intent(this, SettingsActivity::class.java)
        findViewById<TextView>(R.id.settings).setOnClickListener {
            startActivity(intent2)
        }

        val intent3 = Intent(this, InstrumentsActivity::class.java)
        findViewById<TextView>(R.id.instruments).setOnClickListener {
            startActivity(intent3)
        }
        val intent4 = Intent(this, TutorialActivity::class.java)
        findViewById<TextView>(R.id.tutorial).setOnClickListener {
            startActivity(intent4)
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
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?,
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        menu?.add(Menu.NONE, IDM_OPEN, Menu.NONE, "KICK")
        menu?.add(Menu.NONE, IDM_SAVE, Menu.NONE, "SNARE")
    }

    //сообщение:
    override fun onContextItemSelected(item: MenuItem): Boolean {

        val message: CharSequence = when (item.itemId) {
            IDM_OPEN -> "Выбран KICK"
            IDM_SAVE -> "Выбран SNARE"
            else -> return super.onContextItemSelected(item)
        }
        findViewById<TextView>(R.id.txt)?.text=message

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        return true

    }

    override fun onCreateDialog(id: Int): Dialog? {
        val activity = null
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun bytesArrayPart4ToInt(arr : ByteArray, start : Int = 0): Int {
        return arr[start].toInt() + arr[start + 1].toInt()*256 + arr[start + 2].toInt()*256*256 + arr[start + 3].toInt()*256*256*256
    }

    fun getSoundLength (res1: Int) : Int {
        val res: Resources = resources
        val inStream: InputStream = res.openRawResource(res1)
        val wavdata = ByteArray(45)
        inStream.read(wavdata, 0, 45)
        inStream.close()
        if (wavdata.size > 44) {
            val byteRate = bytesArrayPart4ToInt(wavdata, 28)
            val waveSize = bytesArrayPart4ToInt(wavdata, 40)
            if (byteRate != 0) return (waveSize * 1000.0 / byteRate).toInt()
        }
        return 0
    }

    fun playTrack (i: Int, j: Int, delay : Long = 0) {
        var timer : CountDownTimer = object : CountDownTimer(sounds[i][j].delay + delay, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                if (state != "pause") {
                    tracks[i]?.play(sounds[i][j].id,
                        sounds[i][j].volume,
                        sounds[i][j].volume,
                        0,
                        sounds[i][j].loop,
                        sounds[i][j].ratio)
                    Log.d(TAG, "MYMSG: " + i.toString() + " " + j.toString() + " " + sounds[i][0].id.toString())
                }
                if (j < countSounds[i]) playTrack(i, j + 1)
                if (j == countSounds[i] && i == countTracks) state = "ready"
            }
        }.start()
    }

    fun setExample(view: View) { //демонстрация работы как одного трека, так и нескольких звуков (что все работает)
        Toast.makeText(this, "Example set", Toast.LENGTH_SHORT).show()
        sounds[0][0].ratio = 0.5F // по фану, для демонстрации
        sounds[0][0].res = R.raw.file1
        countTracks = 1 // к следующей дорожке

        sounds[1][0].delay = (getSoundLength(sounds[0][0].res)  / sounds[0][0].ratio).toLong() // задержка перед следующим звуком - длина этого, деленное на ratio
        sounds[1][0].res = R.raw.file1
        countSounds[1] = 1 // к следующему звуку
        sounds[1][1].res = R.raw.file2
        sounds[1][1].delay = (getSoundLength(sounds[1][0].res)  / sounds[1][0].ratio).toLong() - 3000 // для демонстрации
        sounds[1][1].loop = 1
        state = "ready"
    }

    fun pause(view: View) {
        if (state == "playing") {
            Toast.makeText(this, "Music paused", Toast.LENGTH_SHORT).show()
            for (i in 0..countTracks) tracks[i].autoPause()
            state = "pause"
        }
    }

    fun playSound(view: View) {
        if (state == "ready") {
            Toast.makeText(this, "Playing compiled music...", Toast.LENGTH_SHORT).show()

            for (i in 0..countTracks) { // очищение и перезаполнение, если играем еще раз
                tracks[i].release()
                tracks[i] = SoundPool(10, AudioManager.STREAM_MUSIC, 0)
            }

            for (i in 0..countTracks) {
                for (j in 0..countSounds[i]) sounds[i][j].id = tracks[i]!!.load(baseContext, sounds[i][j].res, 0) // загрузить i трек, j звук
            }

            if (state != "playing") {
                val start : Long = currentTimeMillis() + 300
                state = "playing"
                for (i in 0..countTracks + 1) playTrack(i, 0, start - currentTimeMillis())
            }
        }
        else if (state == "pause") {
            Toast.makeText(this, "Music unpaused", Toast.LENGTH_SHORT).show()
            state = "playing"
            for (i in 0..countTracks) tracks[i].autoResume()
        }
    }
}