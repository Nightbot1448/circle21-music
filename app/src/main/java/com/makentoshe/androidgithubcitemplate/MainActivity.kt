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
        var id: Int = 0,
        var delay: Long = 0,
        var volume: Float = 0.0F,
        var loop: Int = 0,
        var ratio: Float = 1.0F
    )
    private var ready : Boolean = false
    private val tracks: Array<SoundPool> = Array (100) { SoundPool(10, AudioManager.STREAM_MUSIC, 0) }
    private var countTracks = 0
    private val countSounds: Array<Int> = Array (100) {0}
    private val sounds: Array<Array<SoundInfo>> = Array (100) { Array(1000) {i -> SoundInfo(i, 0, 1.0F, 0, 1.0F) } }
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
        val baos = ByteArrayOutputStream()
        val buff = ByteArray(100)
        inStream.read(buff, 0, buff.size)
        baos.write(buff, 0, buff.size)
        val wavdata = baos.toByteArray()
        baos.close()
        inStream.close()
        if (wavdata.size > 44) {
            val byteRate= bytesArrayPart4ToInt(wavdata, 28)
            val waveSize = bytesArrayPart4ToInt(wavdata, 40)
            Log.d(TAG,"MYMSG: " + (waveSize * 1000 / byteRate).toInt().toString())
            return (waveSize * 1000.0 / byteRate).toInt()
        }
        return 0
    }

    fun playTrack (i: Int, j: Int, delay : Long = 0) {
        var timer : CountDownTimer = object : CountDownTimer(sounds[i][j].delay + delay, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                tracks[i]?.play(sounds[i][j].id, sounds[i][j].volume, sounds[i][j].volume, 0, sounds[i][j].loop, sounds[i][j].ratio)
                Log.d(TAG,"MYMSG: " + i.toString() + " " + j.toString() + " " + sounds[i][0].id.toString())
                if (j < countSounds[i]) {
                    playTrack(i, j + 1)
                }
            }
        }.start()
    }

    fun setExample(view: View) { //демонстрация работы как одного трека, так и нескольких (что все работает)
        ready = true
        sounds[countTracks][countSounds[countTracks]].id = tracks[countTracks]!!.load(baseContext, R.raw.file1, 0) // 0 трек, 0 звук
        countTracks++ // к следующей дорожке
        sounds[countTracks][countSounds[countTracks]].id = tracks[countTracks]!!.load(baseContext, R.raw.file1, 0) // 1 дорожка 0 звук
        countSounds[countTracks]++ // к следующему звуку
        sounds[countTracks][countSounds[countTracks]].id = tracks[countTracks]!!.load(baseContext, R.raw.file2, 0) // 1 дорожка 1 звук
        sounds[0][0].ratio = 0.5F // по фану, для демонстрации
        sounds[0][0].delay = 0 // с начала
        sounds[1][0].delay = (getSoundLength(R.raw.file1)  / sounds[0][0].ratio).toLong() // задержка перед следующим звуком - длина этого, деленное на ratio
        sounds[1][1].delay = (getSoundLength(R.raw.file1)  / sounds[1][0].ratio).toLong() // а тут чтобы онаслоение вышло

    }

    fun playSound(view: View) {
        if (ready) {
            Toast.makeText(this, "Playing compiled music...", Toast.LENGTH_SHORT).show()
            val start : Long = currentTimeMillis() + 300
            for (i in 0..countTracks) {
                playTrack(i, 0, start - currentTimeMillis())
            }
        }
    }
}