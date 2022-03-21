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
import java.util.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private var soundPool: SoundPool? = null
    private val tracks: Array<SoundPool> = Array (100) { SoundPool(1, AudioManager.STREAM_MUSIC, 0) }
    private val countTracks = 0
    private val countSounds: Array<Int> = Array (100) {0}
    private val sounds: Array<Array<Array<Float>>> = Array (100) { Array(1000) {i -> arrayOf(i*1.0F, 1.0F, 1.0F, 0.0F, 1.0F) } }
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
        var i = 0
        tracks[i] = SoundPool(10, AudioManager.STREAM_MUSIC, 0)
        sounds[i][0][0] = tracks[i]!!.load(baseContext, R.raw.file1, 0) * 1.0F
        countSounds[i]++
        sounds[i][1][0] = tracks[i]!!.load(baseContext, R.raw.file1, 0) * 1.0F


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
    fun playTrack (i: Int, j: Int) {
        var timer : CountDownTimer = object : CountDownTimer((sounds[i][j][1].toLong()), 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                tracks[i]?.play(sounds[i][j][0].toInt(), sounds[i][j][2], sounds[i][j][2], 0, sounds[i][j][3].toInt(), sounds[i][j][4])

                if (j < countSounds[i]) {
                    playTrack(i, j + 1)
                }
            }
        }.start()
    }
    fun playSound(view: View) { // TODO: заменить onClick у KICK с этого на правильный, когда будет исправлена раскладка
        Toast.makeText(this, "Playing compiled music...", Toast.LENGTH_SHORT).show()
        sounds[0][0][4] = 0.5F // по фану, для демонстрации
        sounds[0][1][4] = 1.0F
        sounds[0][0][1] = 0.0F // с начала
        sounds[0][1][1] = getSoundLength(R.raw.file1) * 1.0F / sounds[0][0][4] // TODO: заменить на подсчет длины звука
        // TODO: написать алгоритм по одновременному воспроизведению с нескольких дорожек
        var i = 0;
        playTrack(i, 0)
    }
}