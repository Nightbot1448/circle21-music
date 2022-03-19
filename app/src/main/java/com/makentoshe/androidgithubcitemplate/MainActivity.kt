package com.makentoshe.androidgithubcitemplate

import android.app.AlertDialog
import android.app.Dialog
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

import android.view.Menu
import android.view.MenuItem




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
    companion object {
        const val IDM_OPEN = 101
        const val IDM_SAVE = 102
    }

    private lateinit var textView: TextView

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

        //var firstsound = findViewById<Button>(R.id.KICK)

        textView = findViewById(R.id.txt)
        registerForContextMenu(textView)
        //создание контекстного меню

    }
// ниже создание переходов между экранами
    override fun onStart() {
    super.onStart()
    val intent = Intent(this, HelpActivity::class.java)
    findViewById<TextView>(R.id.textView4).setOnClickListener {
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
}

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
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

    //findNavController(R.id.app_graph).navigate(R.id.action_mainActivity_to_helpActivity)


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