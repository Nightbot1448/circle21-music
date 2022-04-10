package com.bigri239.easymusic

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.lang.Exception
import java.lang.System.currentTimeMillis
import java.util.Collections.max
import kotlin.math.abs
import kotlin.math.roundToInt


@Suppress("DEPRECATION")
open class MainActivity : AppCompatActivity(){

    interface Connector {
        fun function(i : Int)
        fun function2 (i: Int, j: Int)
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
    private var currentSound = "bassalbane"
    private var autosave = 10
    private val projects = mutableListOf<String>()
    private val tracks: Array<SoundPool> =
        Array(9) { SoundPool(10, AudioManager.STREAM_MUSIC, 0) }
    private var countTracks = 0
    private var countSounds: Array<Int> = Array(9) { -1 }
    private val emptySound = SoundInfo()
    private var sounds: Array<Array<SoundInfo>> = Array(9) { Array(500) { emptySound } }
    private val resourcesArray : Array<String> = arrayOf("bassalbane", "basscentury", "bassflowers",
        "clapchoppa", "clapforeign", "crashalect", "crashbloods", "crashvinnyx", "fxfreeze",
        "fxgunnes", "hihatcheque", "hihatmystery", "kickartillery", "kickinfinite", "percardonme",
        "percpaolla", "rimchaser", "rimstount", "snarecompas", "snarewoods", "voxanother", "voxgilens")
    private val customArray = arrayListOf<String>()
    private var timer : CountDownTimer? = null
    private lateinit var autoSaver : CountDownTimer
    private var timeRemaining : Long = 0
    private val connector = object : Connector {
        override fun function(i: Int) {
            removeLastSound(i)
        }

        override fun function2(i: Int, j: Int) {
            editSelected(i, j)
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

        btnRem1.setOnClickListener { (currentRecycler(0).adapter as SecondsListAdapter).removeSound(
            Sound(0,0, SoundType.SOUND1, 0, 0))
            setMusicLength()}
        btnRem2.setOnClickListener { (currentRecycler(1).adapter as SecondsListAdapter).removeSound(
            Sound(0,0, SoundType.SOUND1, 1, 0))
            setMusicLength()}
        btnRem3.setOnClickListener { (currentRecycler(2).adapter as SecondsListAdapter).removeSound(
            Sound(0,0, SoundType.SOUND1, 2, 0))
            setMusicLength()}
        btnRem4.setOnClickListener { (currentRecycler(3).adapter as SecondsListAdapter).removeSound(
            Sound(0,0, SoundType.SOUND1, 3, 0))
            setMusicLength()}
        btnRem5.setOnClickListener { (currentRecycler(4).adapter as SecondsListAdapter).removeSound(
            Sound(0,0, SoundType.SOUND1, 4, 0))
            setMusicLength()}
        btnRem6.setOnClickListener { (currentRecycler(5).adapter as SecondsListAdapter).removeSound(
            Sound(0,0, SoundType.SOUND1, 5, 0))
            setMusicLength()}
        btnRem7.setOnClickListener { (currentRecycler(6).adapter as SecondsListAdapter).removeSound(
            Sound(0,0, SoundType.SOUND1, 6, 0))
            setMusicLength()}
        btnRem8.setOnClickListener { (currentRecycler(7).adapter as SecondsListAdapter).removeSound(
            Sound(0,0, SoundType.SOUND1, 7, 0))
            setMusicLength()}
        btnRem9.setOnClickListener { (currentRecycler(8).adapter as SecondsListAdapter).removeSound(
            Sound(0,0, SoundType.SOUND1, 8, 0))
            setMusicLength()}

        val path = filesDir
        var file = File(path, "projects.conf")

        if (file.exists()) {
            val content: String = file.readText()
            projects.addAll(content.split("\n").toTypedArray())
            openProject()
        }
        else {
            projects.add(projectName)
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

        file = File(path, "sounds.conf")

        if (file.exists()) {
            val content: String = file.readText()
            if (content != "") customArray.addAll(content.split("\n").toTypedArray())
        }
        else {
            val content = ""
            FileOutputStream(file).use {
                it.write(content.toByteArray())
            }
        }

        file = File(path, "settings.conf")

        if (file.exists()) {
            val content: String = file.readText()
            if (content != "") autosave = content.split("\n").toTypedArray()[0].toInt()
        }
        else {
            val content = "10"
            FileOutputStream(file).use {
                it.write(content.toByteArray())
            }
        }

        autoSaver = object : CountDownTimer(360001, (autosave * 60000).toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                saveProject()
            }
            override fun onFinish() {
                autoSaver.start()
            }
        }.start()
    }

    // ниже создание переходов между экранами
    override fun onStart() {
        super.onStart()
        val intent = Intent(this, HelpActivity::class.java)
        help.setOnClickListener {
            startActivity(intent)
        }
        val intent1 = Intent(this, AddingfilesActivity::class.java)
        file.setOnClickListener {
            startActivity(intent1)
        }
        val intent2 = Intent(this, SettingsActivity::class.java)
        settings.setOnClickListener {
            startActivity(intent2)
        }
        val intent3 = Intent(this, InstrumentsActivity::class.java)
        instruments.setOnClickListener {
            startActivity(intent3)
        }
        val intent4 = Intent(this, TutorialActivity::class.java)
        tutorial.setOnClickListener {
            startActivity(intent4)
        }
        val intent5 = Intent(this, SigninActivity::class.java)
        account.setOnClickListener {
            startActivity(intent5)
        }
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
        if (getSoundLength(currentSound) <= 5.6 * 60000) {
            if (isReady()) {
                if (state == "unready") state = "ready"
                if (countTracks < x) countTracks = x
                val sound = getSoundParameters(x, countSounds[x] + 1)
                countSounds[x]++
                sounds[x][countSounds[x]] = sound
                var len = ((getSoundLength(sound.res) / sound.ratio) / 40).roundToInt()
                len = if (len > 0) len else 1
                (currentRecycler(x).adapter as SecondsListAdapter).addSound(Sound(
                    (edittextmain1.text.toString().toFloat() / 40).roundToInt(),
                    len,
                    currentColor(countSounds[x]), x, countSounds[x]))
                Log.d(TAG, "MYMSG add: " + getSoundLength(sound.res))
                setMusicLength()
            }
            buttonDelete.setOnClickListener {}
            buttonEdit.setOnClickListener {}
        }
        else Toast.makeText(this, "Oops! Sound is too big!", Toast.LENGTH_SHORT).show()
    }

    private fun isReady () : Boolean {
        return edittextmain1.text.toString().trim().isNotEmpty() && edittextmain3.text.toString()
            .trim().isNotEmpty() && edittextmain4.text.toString().trim().isNotEmpty()
    }

    private fun showProjectDialog() {
        val dialog = Dialog(this, R.style.ThemeOverlay_Material3_Dialog)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_new_project)
        dialog.findViewById<Button>(R.id.create).setOnClickListener {
            newProject = dialog.findViewById<EditText>(R.id.newname).text.toString()
            if (newProject != "" && !projects.contains(newProject)){
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
                clearSounds()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun projectSelectPopupMenuClickListener(menuItem: MenuItem) {
        val itemTitle = menuItem.title.toString()
        if (itemTitle == "New project") showProjectDialog()
        else {
            saveProject()
            projectName = itemTitle
            txt.text = projectName
            clearSounds()
            openProject()
            state = "unready"
            for (i in 0..countTracks) {
                if (sounds[i][0] != emptySound) {
                    state = "ready"
                }
            }
        }
    }

    private fun selectSoundClicked (menuItem: MenuItem) {
        val itemTitle = menuItem.title.toString()
        currentSound = itemTitle
        txt2.text = currentSound
    }

    private fun isRawResource (name : String): Boolean {
        return resourcesArray.contains(name)
    }

    private fun bytesArrayPart4ToInt(arr: ByteArray, start: Int = 0): Int {
        return arr[start].toInt() + arr[start + 1].toInt() * 256 + arr[start + 2].toInt() * 256 * 256 + arr[start + 3].toInt() * 256 * 256 * 256
    }

    private fun clearSounds () {
        for (i in 0..8) { // очистка recycler
            (currentRecycler(i).adapter as SecondsListAdapter).eraseSounds()
        }
        for (i in 0..countTracks) { // очистка данных по звукам
            tracks[i].release()
            tracks[i] = SoundPool(10, AudioManager.STREAM_MUSIC, 0)
            for (j in 0..countSounds[i]) {
                sounds[i][j] = emptySound
            }
            countSounds[i] = -1
        }
        countTracks = 0
        state = "unready"
        setMusicLength()
    }

    private fun getMusicLength (): Long {
        return if (state != "unready") {
            val tracksLengths = mutableListOf<Long>()
            for (i in 0..countTracks) {
                try {
                    tracksLengths.add(0)
                    for (j in 0..countSounds[i]) tracksLengths[i] += sounds[i][j].delay
                    tracksLengths[i] += (getSoundLength(sounds[i][countSounds[i]].res) / sounds[i][countSounds[i]].ratio).toLong()
                }
                catch (e : IndexOutOfBoundsException) {}
            }
            max(tracksLengths)
        } else 0
    }

    private fun setMusicLength(length : Long = getMusicLength()) {
        time.text = if (state != "unready") {
            val millis = length % 1000
            val seconds = (length / 1000) % 60
            val minutes = length / 60000
            val millisString = if(millis >= 100) millis.toString() else {
                if (millis >= 10) "0$millis"
                else "00$millis"
            }
            val secondsString = if (seconds >= 10) seconds.toString() else "0$seconds"
            val minutesString = if (minutes >= 10) minutes.toString() else "0$minutes"
            "$minutesString:$secondsString.$millisString"
        }
        else "00:00.000"
    }

    private fun openProject() {
        try {
            Toast.makeText(this, "Opening project $projectName...", Toast.LENGTH_SHORT).show()
            clearSounds()
            val path = filesDir
            val file = File(path, "$projectName.emproj")
            val content: String = file.readText()
            val tracksContent = content.split("\n").toTypedArray()
            try {
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
                        var len = ((getSoundLength(sounds[i][j].res) / sounds[i][j].ratio) / 40).roundToInt()
                        len = if (len > 0) len else 1
                        (currentRecycler(i).adapter as SecondsListAdapter).addSound(Sound((indentFloat / 40).roundToInt(), len, currentColor(j), i, j))
                    }
                }
                state = "ready"
                setMusicLength()
            }
            catch (e : Exception) {
                countTracks = 0
                countSounds = Array(9) { -1 }
                state = "unready"
                sounds = Array(9) { Array(500) { emptySound } }
                Toast.makeText(this, "Oops! This file seems to be broken!", Toast.LENGTH_SHORT).show()
            }
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
                if (countSounds[i] != -1) {
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
            }
            FileOutputStream(file).use {
                it.write(content.toByteArray())
            }
        }
    }

    fun removeLastSound(i : Int) {
        if (state != "unready") {
            sounds[i][countSounds[i]] = emptySound
            countSounds[i]--
            Log.d(TAG, "MYMSG delete: " + countSounds[i])
            if (countSounds[i] == -1 && countTracks == i) countTracks --
            if (countTracks == -1) {
                countTracks = 0
                state = "unready"
            }
            setMusicLength()
            buttonDelete.setOnClickListener {}
            buttonEdit.setOnClickListener {}
        }
    }

    fun editSelected (i : Int, j : Int) {
        infoAboutSelected(i, j)
        if (countSounds[i] != -1) buttonDelete.setOnClickListener { deleteSelected(i, j) }
        else buttonDelete.setOnClickListener {}
        if (countSounds[i] != -1) buttonEdit.setOnClickListener { changeSelected(i, j) }
        else buttonEdit.setOnClickListener {}
    }

    private fun infoAboutSelected (i : Int, j : Int) {
        val sound = sounds[i][j]
        currentSound = sound.res
        txt2.text = currentSound
        edittextmain1.setText(if (j != 0) (sound.delay - getSoundLength(sounds[i][j - 1].res) / sounds[i][j - 1].ratio).toInt().toString()
        else sound.delay.toString())
        edittextmain3.setText((sound.volume * 100).toInt().toString())
        edittextmain4.setText((100 / sound.ratio).toString())
    }

    private fun deleteSelected (i : Int, j: Int) {
        if (j <= countSounds[i]) {
            val delay : Long = sounds[i][j].delay + sounds[i][j + 1].delay
            sounds[i][j] = sounds[i][j + 1]
            sounds[i][j].delay = delay
            for (k in (j + 1)..countSounds[i]) sounds[i][k] = sounds[i][k + 1]
            countSounds[i]--
            if (countSounds[i] == -1) {
                if (sounds.contentEquals(Array(100) { Array(500) { emptySound } })) state = "unready"
                if (countTracks == i && countTracks != 0) countTracks --
            }
            setMusicLength()
            (currentRecycler(i).adapter as SecondsListAdapter).eraseSounds()
            if (countSounds[i] != -1) {
                for (y in 0..countSounds[i]) {
                    val sound = sounds[i][y]
                    val indentFloat : Float = if (y != 0) sound.delay - getSoundLength(sounds[i][y - 1].res) / sounds[i][y - 1].ratio
                    else sound.delay.toFloat()
                    var len = ((getSoundLength(sounds[i][y].res) / sounds[i][y].ratio) / 40).roundToInt()
                    len = if (len > 0) len else 1
                    (currentRecycler(i).adapter as SecondsListAdapter).addSound(Sound(
                        (indentFloat / 40).roundToInt(), len, currentColor(y), i, y))
                }
            }
        }
    }

    private fun changeSelected (i : Int, j: Int) {
        if (j <= countSounds[i]) {
            sounds[i][j] = getSoundParameters(i, j)
            setMusicLength()
            (currentRecycler(i).adapter as SecondsListAdapter).eraseSounds()
            for (y in 0..countSounds[i]) {
                val sound = sounds[i][y]
                val indentFloat : Float = if (y != 0) sound.delay - getSoundLength(sounds[i][y - 1].res) / sounds[i][y - 1].ratio
                else sound.delay.toFloat()
                var len = ((getSoundLength(sounds[i][y].res) / sounds[i][y].ratio) / 40).roundToInt()
                len = if (len > 0) len else 1
                (currentRecycler(i).adapter as SecondsListAdapter).addSound(Sound(
                    (indentFloat / 40).roundToInt(), len, currentColor(y), i, y))
            }
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

    private fun getSoundLength(name: String): Long {
        val metaRetriever = MediaMetadataRetriever()
        if (isRawResource(name)) {
            val inStream: InputStream = resources.openRawResource(resources.getIdentifier(name, "raw", packageName))
            val data = inStream.let { readBytes(it) }
            val file = File(filesDir, "sound.wav")
            FileOutputStream(file).use {
                it.write(data)
            }
            metaRetriever.setDataSource(File(filesDir, "sound.wav").absolutePath)
            /*val wavdata = ByteArray(100)
            inStream.read(wavdata, 0, 100)
            inStream.close()
            val byteRate = bytesArrayPart4ToInt(wavdata, 28)
            val dataArray = "data".toByteArray() // сколько бы я не пытался, но оно не работает(
            val startData = wavdata.indexOf(dataArray[0])
            val start = if (wavdata[startData + 1] == dataArray[1]) startData + 4
            else wavdata.lastIndexOf(dataArray[0]) + 4
            val waveSize = bytesArrayPart4ToInt(wavdata, start)
            if (byteRate != 0) abs((waveSize * 1000.0 / byteRate).toLong())
            else 0*/
        }
        else metaRetriever.setDataSource(File(filesDir, "$name.wav").absolutePath)
        return abs(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toLong() - 1)
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
            }
        }.start()
    }

    private fun getSoundParameters(x : Int, y : Int): SoundInfo {
        val res : String = currentSound
        var volume : Float = abs(edittextmain3.text.toString().toFloat() / 100)
        if (volume > 1) volume = 1.0F
        if (edittextmain4.text.toString().toFloat() < 12.5) edittextmain4.setText(12.5F.toString())
        else if (edittextmain4.text.toString().toFloat() > 800) edittextmain4.setText(800.toString())
        val ratio : Float = abs(100 / (edittextmain4.text.toString().toFloat()))
        Log.d(TAG, "MYMSG param: $res")
        val delay : Long = if (y > 0) (edittextmain1.text.toString().toFloat() + getSoundLength(sounds[x][y - 1].res) / sounds[x][y - 1].ratio).toLong()
        else edittextmain1.text.toString().toLong()
        return SoundInfo(res, 0, delay, volume, 0, ratio)
    }

    private fun setTimer (length: Long = getMusicLength()) {
        val len = getMusicLength()
        timer = object : CountDownTimer(length + 100, 1) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                if (len - timeRemaining >= 100) setMusicLength(len - timeRemaining - 100)
            }
            override fun onFinish() {
                timeRemaining = 0
                setMusicLength()
                state = "ready"
            }
        }.start()
    }

    fun playSound(view: View) {
        if (state == "ready") {
            played += 1
            Toast.makeText(this, "Playing compiled music...", Toast.LENGTH_SHORT).show()
            saveProject()
            setMusicLength()

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
                setTimer()
                val start: Long = currentTimeMillis() + 100
                state = "playing"
                for (i in 0..countTracks) if (countSounds[i] != -1) playTrack(i, 0, start - currentTimeMillis())
            }
        }
        else if (state == "pause") {
            Toast.makeText(this, "Music unpaused", Toast.LENGTH_SHORT).show()
            setTimer(timeRemaining)
            state = "playing"
            for (i in 0..countTracks) tracks[i].autoResume()
        }
    }

    fun resetPlaying(view: View) {
        timer?.cancel()
        Toast.makeText(this, "Playing halted", Toast.LENGTH_SHORT).show()
        for (i in 0..countTracks) { // очищение и перезаполнение, если играем еще раз
            tracks[i].autoPause()
            tracks[i].release()
            tracks[i] = SoundPool(10, AudioManager.STREAM_MUSIC, 0)
            state = "ready"
        }
    }

    fun pause(view: View) {
        if (state == "playing") {
            timer?.cancel()
            Toast.makeText(this, "Music paused", Toast.LENGTH_SHORT).show()
            for (i in 0..countTracks) tracks[i].autoPause()
            state = "pause"
        }
    }

    fun saveProjectUI (view: View) {
        saveProject()
        if (state != "unready") Toast.makeText(this, "Saving project...", Toast.LENGTH_SHORT).show()
    }

    fun createSelectProjectPopupMenu(v: View) {
        val popupMenu = PopupMenu(this, v)
        for (i in projects.indices) popupMenu.menu.add(projects[i])
        popupMenu.inflate(R.menu.popupmenu)
        popupMenu.setOnMenuItemClickListener { projectSelectPopupMenuClickListener(it); true }
        popupMenu.show()
    }

    fun selectSound(view: View) {
        val popupMenu = PopupMenu(this, view)
        for (i in customArray.indices) popupMenu.menu.add(customArray[i])
        for (i in resourcesArray.indices) popupMenu.menu.add(resourcesArray[i])
        popupMenu.setOnMenuItemClickListener { selectSoundClicked(it); true }
        popupMenu.show()
    }
}