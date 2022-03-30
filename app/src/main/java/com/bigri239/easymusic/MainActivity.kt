package com.bigri239.easymusic

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.System.currentTimeMillis
import java.util.*
import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

import android.widget.LinearLayout.HORIZONTAL
import androidx.recyclerview.widget.LinearLayoutManager


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
    private var projectName = "project1"
    private val tracks: Array<SoundPool> = Array (100) { SoundPool(10, AudioManager.STREAM_MUSIC, 0) }
    private var countTracks = 0
    private val countSounds: Array<Int> = Array (100) {0}
    private val sounds: Array<Array<SoundInfo>> = Array (100) { Array(1000) {i -> SoundInfo(0, i + 1, 0, 1.0F, 0, 1.0F) } }
    private var progressBar: ProgressBar? = null
    private var txtView: TextView? = null
    private lateinit var textView: TextView

    private var mAdapter: RecyclerAdapter? = null
    private var mRecyclerView: RecyclerView? = null

    companion object {
        const val IDM1 = 101
        const val IDM2 = 102
        const val IDM3 = 103
        const val MENU1 = 11
        const val MENU2 = 12
        const val MENU3 = 13
    }

    //опишем создание контекстных меню
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        //var firstsound = findViewById<Button>(R.id.KICK)

        mRecyclerView = findViewById(R.id.recyclerViewhor)
        mRecyclerView?.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL, false)
        val dataset = arrayOfNulls<String>(50)
        for (i in dataset.indices) {
            dataset[i] = "item$i"
        }
        mAdapter = RecyclerAdapter(dataset, this)
        mRecyclerView?.adapter = mAdapter

        textView = findViewById(R.id.txt)
        registerForContextMenu(textView)


        //создание контекстного меню
        val animals = mutableListOf(
            "Kick", "Type1", "Snare",
            "Type1", "Hihat", "Type1",
            "Loop", "Type1", "Bass",
            "Type1", "808", "Type1",
            "+", "Type1","+",
            "Type1"
        )

        // initialize grid layout manager
        GridLayoutManager(
            this, // context
            2, // span count
            RecyclerView.VERTICAL, // orientation
            false // reverse layout
        ).apply {
            // specify the layout manager for recycler view
            findViewById<RecyclerView>(R.id.recyclerView).layoutManager = this
        }

        // finally, data bind the recycler view with adapter
        findViewById<RecyclerView>(R.id.recyclerView).adapter = RecyclerViewAdapter(animals)

        val stripes = mutableListOf(
            "Kick", "Type1", "Snare",
            "Type1", "Hihat", "Type1",
            "Loop", "Type1", "Bass",
            "Type1", "808", "Type1",
            "+", "Type1","+",
            "Type1"
        )

//        // initialize grid layout manager
//        GridLayoutManager(
//            this, // context
//            2, // span count
//            RecyclerView.VERTICAL, // orientation
//            false // reverse layout
//        ).apply {
//            // specify the layout manager for recycler view
//            findViewById<RecyclerView>(R.id.scroll1).layoutManager = this
//        }
//
//        // finally, data bind the recycler view with adapter
//        findViewById<RecyclerView>(R.id.scroll1).adapter = RecyclerViewAdapter(stripes)



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
        }
            val intent14 = Intent(this, RecoveryActivity::class.java)
            findViewById<TextView>(R.id.account).setOnClickListener {
                startActivity(intent14)
            }


    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?,
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        menu?.add(Menu.NONE, IDM1, Menu.NONE, "project1")
        menu?.add(Menu.NONE, IDM2, Menu.NONE, "project2")
        menu?.add(Menu.NONE, IDM3, Menu.NONE, "project3")

        super.onCreateContextMenu(menu, v, menuInfo)

        menu?.add(Menu.NONE, MENU1, Menu.NONE, "kick")
        menu?.add(Menu.NONE, MENU2, Menu.NONE, "snare")
        menu?.add(Menu.NONE, MENU3, Menu.NONE, "hihat")

    }
    private var tracknumber = 1
    //сообщение:
    override fun onContextItemSelected(item: MenuItem): Boolean {

        val message: CharSequence = when (item.itemId) { // TODO: написать добавление элемента в меню
            IDM1 -> "project1"
            IDM2 -> "project2"
            IDM3 -> "project3"
            else -> return super.onContextItemSelected(item)
        }
//        switch (item.getItemId()) {
//            case IDM1 :
//
//            break;
//            case IDM2 :
//
//            break;
//            case MENU_COLOR_BLUE :
//
//            break;
//        }


            findViewById<TextView>(R.id.txt)?.text = message
        projectName = message as String
        if (message == "project2"){
            tracknumber = 2
        }
        if (message == "project3"){
            tracknumber = 3
        }



        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        findViewById<TextView>(R.id.textView)?.text= tracknumber.toString()
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
                if (j == countSounds[i] && i == countTracks) {
                    var timer1 : CountDownTimer = object : CountDownTimer((getSoundLength(sounds[i][j].res) * (sounds[i][j].loop + 1) / sounds[i][j].ratio).toLong(), 1000) {
                        override fun onTick(millisUntilFinished: Long) {}
                        override fun onFinish() {
                            state = "ready"
                        }
                    }.start()
                }
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
                for (i in 0..countTracks) playTrack(i, 0, start - currentTimeMillis())
            }
        }
        else if (state == "pause") {
            Toast.makeText(this, "Music unpaused", Toast.LENGTH_SHORT).show()
            state = "playing"
            for (i in 0..countTracks) tracks[i].autoResume()
        }
    }

    fun openProject(view: View) {
        try {
            Toast.makeText(this, "Opening project...", Toast.LENGTH_SHORT).show()
            val path = getFilesDir()
            val file = File(path, projectName + ".emproj")
            val content : String = file.readText()
            val tracks_content = content.split("\n").toTypedArray()
            countTracks = tracks_content.size - 1
            for (i in tracks_content.indices) {
                val sounds_content = tracks_content[i].split(";").toTypedArray()
                countSounds[i] = sounds_content.size - 1
                for (j in sounds_content.indices) {
                    val params = sounds_content[j].split(" ").toTypedArray()
                    sounds[i][j] = SoundInfo(params[0].toInt(), params[1].toInt(), params[2].toLong(), params[3].toFloat(), params[4].toInt(), params[5].toFloat())
                }
            }
            state = "ready"
        }
        catch (e: IOException) {
            Toast.makeText(this, "No such file!", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveProject(view: View) {
        if (state != "unready") {
            Toast.makeText(this, "Saving project...", Toast.LENGTH_SHORT).show()
            val path = getFilesDir()
            val file = File(path, projectName + ".emproj")
            var content = ""
            for (i in 0..countTracks) {
                for (j in 0..countSounds[i]) {
                    content += sounds[i][j].res.toString() + " "
                    content += sounds[i][j].id.toString() + " "
                    content += sounds[i][j].delay.toString() + " "
                    content += sounds[i][j].volume.toString() + " "
                    content += sounds[i][j].loop.toString() + " "
                    content += sounds[i][j].ratio.toString()
                    if (j != countSounds[i]) content += ";"
                }
                if (i != countTracks) content += "\n"
            }
            FileOutputStream(file).use {
                it.write(content.toByteArray())
            }
        }
    }

    fun saveMusic(view: View) {
        if (state != "unready") {
            Toast.makeText(this, "Saving music...", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, "Oops! Not ready yet! :(", Toast.LENGTH_SHORT).show()
        }
    }
}