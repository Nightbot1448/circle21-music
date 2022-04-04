package com.bigri239.easymusic

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bigri239.easymusic.recyclers.SecondsListAdapter
import com.bigri239.easymusic.recyclers.Sound
import com.bigri239.easymusic.recyclers.SoundType
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.System.currentTimeMillis
import kotlin.math.roundToInt


@Suppress("DEPRECATION")
open class MainActivity : AppCompatActivity(){

    interface Connector {
        fun function(i : Int)
    }

    data class SoundInfo(
        var res: String = "",
        var id: Int = 0,
        var delay: Long = 0,
        var volume: Float = 0.0F,
        var loop: Int = 0,
        var ratio: Float = 1.0F,
    )

    private var newProject = ""
    private var state: String = "unready"
    private var played = 0
    private var projectName = "projectDefault"
    private val projects = mutableListOf<String>()
    private val tracks: Array<SoundPool> =
        Array(9) { SoundPool(10, AudioManager.STREAM_MUSIC, 0) }
    private var countTracks = 0
    private val countSounds: Array<Int> = Array(9) { -1 }
    private val sounds: Array<Array<SoundInfo>> =
        Array(100) { Array(100) { i -> SoundInfo("", i + 1, 0, 1.0F, 0, 1.0F) } }
    private var viewClickListener = View.OnClickListener { v -> create_select_project_popup_menu(v) }
    private val connector = object : Connector {
        override fun function(i: Int) {
            removeLastSound(i)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        for (i in 0..8) {
            currentRecycler(i).adapter = SecondsListAdapter(connector)
            (currentRecycler(i).adapter as SecondsListAdapter).notifyDataSetChanged()
        }

        btnAdd1.setOnClickListener { addSound(0) }
        btnAdd2.setOnClickListener { addSound(1) }
        btnAdd3.setOnClickListener { addSound(2) }
        btnAdd4.setOnClickListener { addSound(3) }
        btnAdd5.setOnClickListener { addSound(4) }
        btnAdd6.setOnClickListener { addSound(5) }
        btnAdd7.setOnClickListener { addSound(6) }
        btnAdd8.setOnClickListener { addSound(7) }
        btnAdd9.setOnClickListener { addSound(8) }

        val textView = findViewById<TextView>(R.id.txt)
        textView.setOnClickListener(viewClickListener)
        try {
            val path = filesDir
            val file = File(path, "projects.conf")
            val content: String = file.readText()
            projects.addAll(content.split("\n").toTypedArray())
            openProject()
        }
        catch (e: IOException) {
            projects.add("projectDefault")
            val path = filesDir
            val file = File(path, "projects.conf")
            var content = ""
            for (i in projects.indices) {
                content += projects[i]
                if (i != projects.size - 1) content += "\n"
            }
            FileOutputStream(file).use {
                it.write(content.toByteArray())
            }
            saveProject()
        }
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
        val intent14 = Intent(this, SigninActivity::class.java)
        findViewById<TextView>(R.id.account).setOnClickListener {
            startActivity(intent14)
        }
    }

    override fun onCreateDialog(id: Int): Dialog {
        val activity = null
        return activity?.let {
            AlertDialog.Builder(it).create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun currentRecycler (i : Int) : RecyclerView {
        val recyclerCurrent : RecyclerView = when(i) {
            0 -> recyclerViewhor1
            1 -> recyclerViewhor2
            2 -> recyclerViewhor3
            3 -> recyclerViewhor4
            4 -> recyclerViewhor5
            5 -> recyclerViewhor6
            6 -> recyclerViewhor7
            7 -> recyclerViewhor8
            else -> recyclerViewhor9
        }
        return recyclerCurrent
    }

    private fun currentColor (j : Int) : SoundType {
        val color : SoundType = when (j % 5) {
            0 -> SoundType.SOUND1
            1 -> SoundType.SOUND2
            2 -> SoundType.SOUND3
            3 -> SoundType.SOUND4
            else -> SoundType.SOUND5
        }
        return color
    }

    private fun addSound (x : Int) {
        if (state == "unready") state = "ready"
        if (countTracks < x) countTracks = x
        val sound = getSoundParameters(x, countSounds[x] + 1)
        countSounds[x]++
        sounds[x][countSounds[x]] = sound
        (currentRecycler(x).adapter as SecondsListAdapter).addSound(Sound(
            (edittextmain1.text.toString().toFloat() / 100).roundToInt(),
            ((getSoundLength(sound.res) / sound.ratio) / 100).toInt() + 1,
            currentColor(countSounds[x]), x))
    }

    private fun showProjectDialog() {
        val dialog = Dialog(this, R.style.ThemeOverlay_Material3_Dialog)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_new_project)
        dialog.findViewById<Button>(R.id.create).setOnClickListener {
            newProject = dialog.findViewById<EditText>(R.id.newname).text.toString()
            saveProject()
            projectName = newProject
            projects.add(projectName)
            txt.text = projectName
            Toast.makeText(applicationContext, "You created $projectName", Toast.LENGTH_SHORT).show()
            val path = filesDir
            val file = File(path, "projects.conf")
            val content = file.readText() + "\n" + projectName
            FileOutputStream(file).use {
                it.write(content.toByteArray())
            }

            for (i in 0..countTracks) { // очистка данных по звукам
                tracks[i].release()
                tracks[i] = SoundPool(10, AudioManager.STREAM_MUSIC, 0)
                for (j in 0..countSounds[i]) {
                    sounds[i][j] = SoundInfo("", i + 1, 0, 1.0F, 0, 1.0F)
                }
                countSounds[i] = -1
            }
            countTracks = 0
            state = "unready"
            clearRecycler()
            saveProject()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun project_select_popup_menu_click_listener(menuItem: MenuItem) {
        val itemTitle = menuItem.title.toString()
        if (itemTitle == "New project") {
            showProjectDialog()
        }
        else {
            saveProject()
            projectName = itemTitle
            txt.text = projectName
            clearRecycler()
            openProject()
            state = "unready"
            for (i in 0..countTracks) {
                if (sounds[i][0] != SoundInfo("", i + 1, 0, 1.0F, 0, 1.0F)) {
                    state = "ready"
                }
            }
        }
    }

    private fun create_select_project_popup_menu(v: View) {
        val popupMenu = PopupMenu(this, v)
        for (i in projects.indices) popupMenu.menu.add(projects[i])
        popupMenu.inflate(R.menu.popupmenu)
        popupMenu.setOnMenuItemClickListener { project_select_popup_menu_click_listener(it); true }
        popupMenu.show()
    }

    private fun isRawResource (name : String): Boolean {
        val resourcesArray : Array<String> = arrayOf("bassalbane", "basscentury", "bassflowers",
            "clapchoppa", "clapforeign", "crashalect", "crashbloods", "crashvinnyx", "fxfreeze",
            "fxgunnes", "hihatcheque", "hihatmystery", "kickartillery", "kickinfinite", "percardonme",
            "percpaolla", "rimchaser", "rimstount", "snarecompas", "snarewoods", "voxanother",
            "voxgilens")
        return resourcesArray.contains(name)
    }

    private fun bytesArrayPart4ToInt(arr: ByteArray, start: Int = 0): Int {
        return arr[start].toInt() + arr[start + 1].toInt() * 256 + arr[start + 2].toInt() * 256 * 256 + arr[start + 3].toInt() * 256 * 256 * 256
    }

    private fun clearRecycler () {
        for (i in 0..8) {
            (currentRecycler(i).adapter as SecondsListAdapter).eraseSounds()
        }
    }

    private fun openProject() {
        try {
            Toast.makeText(this, "Opening project $projectName...", Toast.LENGTH_SHORT).show()
            clearRecycler()
            val path = filesDir
            val file = File(path, "$projectName.emproj")
            val content: String = file.readText()
            val tracksContent = content.split("\n").toTypedArray()
            countTracks = tracksContent.size - 1
            for (i in tracksContent.indices) {
                val soundsContent = tracksContent[i].split(";").toTypedArray()
                countSounds[i] = soundsContent.size - 1
                for (j in soundsContent.indices) {
                    val params = soundsContent[j].split(" ").toTypedArray()
                    sounds[i][j] = SoundInfo(
                        params[0],
                        params[1].toInt(),
                        params[2].toLong(),
                        params[3].toFloat(),
                        params[4].toInt(),
                        params[5].toFloat()
                    )
                    val indentFloat : Float = if (j != 0) params[2].toLong() - getSoundLength(sounds[i][j - 1].res) / sounds[i][j - 1].ratio
                    else params[2].toFloat()
                    (currentRecycler(i).adapter as SecondsListAdapter).addSound(Sound(
                        (indentFloat / 100).roundToInt(),
                        ((getSoundLength(sounds[i][j].res) / sounds[i][j].ratio) / 100).toInt() + 1,
                        currentColor(j), i))
                }
            }
            state = "ready"
        } catch (e: IOException) {
            Toast.makeText(this, "No such file!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProject() {
        if (state != "unready") {
            val path = filesDir
            val file = File(path, "$projectName.emproj")
            var content = ""
            for (i in 0..countTracks) {
                for (j in 0..countSounds[i]) {
                    content += sounds[i][j].res + " "
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

    fun removeLastSound(i : Int) {
        sounds[i][countSounds[i]] = SoundInfo("", i + 1, 0, 1.0F, 0, 1.0F)
        countSounds[i]--
    }

    private fun getSoundLength(name: String): Long {
        val inStream: InputStream = if (isRawResource(name)) resources.openRawResource(resources.getIdentifier(name, "raw", packageName))
        else File(filesDir,"$name.wav").inputStream()
        val wavdata = ByteArray(45)
        inStream.read(wavdata, 0, 45)
        inStream.close()
        if (wavdata.size > 44) {
            val byteRate = bytesArrayPart4ToInt(wavdata, 28)
            val waveSize = bytesArrayPart4ToInt(wavdata, 40)
            if (byteRate != 0) return (waveSize * 1000.0 / byteRate).toLong()
        }
        return 0
    }

    private fun playTrack(i: Int, j: Int, delay: Long = 0) {
        val started = played
        object : CountDownTimer(sounds[i][j].delay + delay, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                val sound = sounds[i][j]
                if (state != "pause" && started == played) {
                    tracks[i].play(sound.id, sound.volume, sound.volume, 0, 0, sound.ratio)
                    Log.d(TAG, "MYMSG play: $i $j " + sounds[i][0].res)
                }
                if (j < countSounds[i] && started == played) playTrack(i, j + 1)
                if (j == countSounds[i] && i == countTracks && state != "pause" && started == played) {
                    object : CountDownTimer(
                        (getSoundLength(sound.res) * (sound.loop + 1) / sound.ratio).toLong(), 1000) {
                        override fun onTick(millisUntilFinished: Long) {}
                        override fun onFinish() {
                            state = "ready"
                        }
                    }.start()
                }
            }
        }.start()
    }

    private fun getSoundParameters(x : Int, y : Int): SoundInfo {
        val res : String = edittextmain2.text.toString()
        val volume : Float = edittextmain3.text.toString().toFloat() / 100
        val ratio : Float = 100 / (edittextmain4.text.toString().toFloat())
        Log.d(TAG, "MYMSG param: $res")
        val delay : Long = if (y > 0) (edittextmain1.text.toString().toFloat() + getSoundLength(sounds[x][y - 1].res) / sounds[x][y - 1].ratio).toLong()
        else edittextmain1.text.toString().toLong()
        return SoundInfo(res, 0, delay, volume, 0, ratio)
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
            played += 1
            Toast.makeText(this, "Playing compiled music...", Toast.LENGTH_SHORT).show()
            saveProject()

            for (i in 0..countTracks) { // очищение и перезаполнение, если играем еще раз
                tracks[i].release()
                tracks[i] = SoundPool(10, AudioManager.STREAM_MUSIC, 0)
            }

            for (i in 0..countTracks) {
                for (j in 0..countSounds[i]) {
                    val res = sounds[i][j].res
                    if (isRawResource(res)) sounds[i][j].id = tracks[i].load(
                        baseContext,
                        resources.getIdentifier(res, "raw", packageName),
                        0
                    ) // загрузить i трек, j звук, если это ресурс
                    else sounds[i][j].id = tracks[i].load("$filesDir/$res.wav",0) // загрузить i трек, j звук, если это пользовательский звук
                }
            }

            if (state != "playing") {
                val start: Long = currentTimeMillis() + 100
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

    fun saveProjectUI (view: View) {
        saveProject()
        if (state != "unready") Toast.makeText(this, "Saving project...", Toast.LENGTH_SHORT).show()
    }

    fun resetPlaying(view: View) {
        Toast.makeText(this, "Playing halted", Toast.LENGTH_SHORT).show()
        for (i in 0..countTracks) { // очищение и перезаполнение, если играем еще раз
            tracks[i].autoPause()
            tracks[i].release()
            tracks[i] = SoundPool(10, AudioManager.STREAM_MUSIC, 0)
            state = "ready"
        }
    }
}