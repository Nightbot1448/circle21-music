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
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.bigri239.easymusic.visualizer.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.lang.System.currentTimeMillis
import java.util.Collections.max
import kotlin.math.abs
import kotlin.math.roundToInt

@Suppress("DEPRECATION")
open class MainActivity : AppCompatActivity(){

    interface Connector {
        fun function (i: Int, j: Int)
    }

    data class SoundInfo(
        var res: String = "",
        var id: Int = 0,
        var delay: Long = 0,
        var volume: Float = 0.0F,
        var loop: Int = 0,
        var ratio: Float = 1.0F,
        var len: Long = 0
    )

    private var newProject = ""
    private var state = "unready"
    private var played = 0
    private var touchedRvTag = 0
    private var projectName = "projectDefault"
    private var currentSound = "bassalbane"
    private var autoSaveInterval = 10
    private val projects = mutableListOf<String>()
    private val tracks: Array<SoundPool> =
        Array(9) { SoundPool(10, AudioManager.STREAM_MUSIC, 0) }
    private var maxTracks = 0
    private var maxSounds: Array<Int> = Array(9) { -1 }
    private val emptySound = SoundInfo()
    private var sounds: Array<Array<SoundInfo>> = Array(9) { Array(1000) { emptySound } }
    private val resourcesArray : Array<String> = arrayOf("bassalbane", "basscentury", "bassflowers",
        "clapchoppa", "clapforeign", "crashalect", "crashbloods", "crashvinnyx", "fxfreeze",
        "fxgunnes", "hihatcheque", "hihatmystery", "kickartillery", "kickinfinite", "percardonme",
        "percpaolla", "rimchaser", "rimstount", "snarecompas", "snarewoods", "voxanother",
        "voxgilens")
    private val customArray = arrayListOf<String>()
    private var timer : CountDownTimer? = null
    private lateinit var autoSaver : CountDownTimer
    private var timeRemaining : Long = 0
    private val connector = object : Connector {
        override fun function(i: Int, j: Int) {
            editSelected(i, j)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val yourScrollListener: RecyclerView.OnScrollListener =
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (recyclerView.tag as Int == touchedRvTag) {
                        for (noOfRecyclerView in 0..8) {
                            if (noOfRecyclerView != recyclerView.tag as Int) {
                                val tempRecyclerView =
                                    constraintLayout.findViewWithTag(noOfRecyclerView)
                                            as RecyclerView
                                tempRecyclerView.scrollBy(dx, dy)
                            }
                        }
                    }
                }
            }

        val yourTouchListener: OnItemTouchListener = object : OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                touchedRvTag = rv.tag as Int
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        }

        Thread {
            val displayMetrics = resources.displayMetrics
            val scale: Float = displayMetrics.density
            val pixelsWidth = displayMetrics.widthPixels - (239 * scale + 0.5f).toInt()

            for (i in 0..8) {
                val recycler = currentRecycler(i)
                recycler.layoutParams = LinearLayout.LayoutParams(pixelsWidth,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
                recycler.adapter = SecondsListAdapter(connector, (pixelsWidth / scale).toInt())
                recycler.tag = i
                recycler.addOnScrollListener(yourScrollListener)
                recycler.addOnItemTouchListener(yourTouchListener)
            }
        }.start()
    }

    override fun onStart() {
        super.onStart()

        val path = filesDir
        var currentFile = File(filesDir, "terms.conf")

        if (currentFile.exists()) {
            val content: String = currentFile.readText()
            if (content != "1") {
                val intent0 = Intent(this, TermsActivity::class.java)
                intent0.putExtra("isStart", "true")
                startActivity(intent0)
            }
        }
        else {
            val intent0 = Intent(this, TermsActivity::class.java)
            intent0.putExtra("isStart", "true")
            startActivity(intent0)
        }

        currentFile = File(path, "projects.conf")

        if (currentFile.exists()) {
            val content: String = currentFile.readText()
            projects.addAll(content.split("\n").toTypedArray())
        }
        else {
            projects.add(projectName)
            FileOutputStream(currentFile).write(projectName.toByteArray())
            rawResourceToFile("project", "projectDefault.emproj")
        }

        currentFile = File(path, "projectDefault.emproj")

        if (!currentFile.exists())
            rawResourceToFile("project", "projectDefault.emproj")

        currentFile = File(path, "sounds.conf")

        if (currentFile.exists()) {
            val content: String = currentFile.readText()
            if (content != "") customArray.addAll(content.split("\n").toTypedArray())
        }
        else FileOutputStream(currentFile).write("".toByteArray())

        currentFile = File(path, "settings.conf")

        if (currentFile.exists()) {
            val content: String = currentFile.readText()
            if (content != "") autoSaveInterval = content.split("\n").toTypedArray()[0]
                .toInt()
        }
        else FileOutputStream(currentFile).write("10".toByteArray())

        for (i in 1..9) {
            findViewById<Button>(resources.getIdentifier("btnAdd$i", "id",
                packageName)).setOnClickListener { addSound(i - 1) }
            findViewById<Button>(resources.getIdentifier("btnRem$i", "id",
                packageName)).setOnClickListener { deleteSelected(i - 1, maxSounds[i - 1]) }
        }

        autoSaver = object : CountDownTimer(360001, (autoSaveInterval * 60000)
            .toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                saveProject()
            }
            override fun onFinish() {
                autoSaver.start()
            }
        }.start()

        openProject()

        val intent = Intent(this, HelpActivity::class.java)
        help.setOnClickListener {
            help.isClickable = false
            startActivity(intent)
        }

        val intent1 = Intent(this, AddingfilesActivity::class.java)
        file.setOnClickListener {
            file.isClickable = false
            startActivity(intent1)
        }

        val intent2 = Intent(this, SettingsActivity::class.java)
        settings.setOnClickListener {
            settings.isClickable = false
            startActivity(intent2)
        }

        val intent3 = Intent(this, TutorialActivity::class.java)
        tutorial.setOnClickListener {
            tutorial.isClickable = false
            startActivity(intent3)
        }

        val intent4 = Intent(this, SigninActivity::class.java)
        account.setOnClickListener {
            account.isClickable = false
            startActivity(intent4)
        }
    }

    override fun onStop() {
        super.onStop()
        saveProject()
    }

    private fun currentRecycler (i : Int) : RecyclerView {
        return when(i) {
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
    }

    private fun currentColor (i : Int, j : Int) : SoundType {
        val names = mutableSetOf<String>()
        for (k in sounds[i]) names.add(k.res)
        val number = names.indexOf(sounds[i][j].res)
        return when (i % 3) {
            0 -> when (number % 5) {
                0 -> SoundType.SOUND1
                1 -> SoundType.SOUND2
                2 -> SoundType.SOUND3
                3 -> SoundType.SOUND4
                else -> SoundType.SOUND5
            }
            1 -> when (number % 5) {
                0 -> SoundType.SOUND11
                1 -> SoundType.SOUND12
                2 -> SoundType.SOUND13
                3 -> SoundType.SOUND14
                else -> SoundType.SOUND15
            }
            else -> when (number % 5) {
                0 -> SoundType.SOUND21
                1 -> SoundType.SOUND22
                2 -> SoundType.SOUND23
                3 -> SoundType.SOUND24
                else -> SoundType.SOUND25
            }
        }
    }

    private fun addSound (x : Int) {
        if (getSoundLength(currentSound) <= 5600) {
            if (isReady()) {
                val prevMusicLength = getMusicLength()
                if (state == "unready") state = "ready"
                if (maxTracks < x) maxTracks = x
                val sound = getSoundParameters(x, maxSounds[x] + 1)
                maxSounds[x]++
                sounds[x][maxSounds[x]] = sound
                var len = (sound.len / 10.0).roundToInt()
                len = if (len > 0) len else 1
                val indent = if (maxSounds[x] > 0) sound.delay - sounds[x][maxSounds[x] - 1].len
                else sound.delay
                (currentRecycler(x).adapter as SecondsListAdapter).addSound(
                    Sound(
                    (indent / 10.0).roundToInt(),
                    len,
                    currentColor(x, maxSounds[x]), x, maxSounds[x]),
                    prevMusicLength < getMusicLength())
                if (prevMusicLength < getMusicLength()) setMusicLength()
            }
            buttonDelete.setOnClickListener {}
            buttonEdit.setOnClickListener {}
        }
        else Toast.makeText(this, "Oops! Sound is too big!", Toast.LENGTH_SHORT).show()
    }

    private fun setMaxLength () {
        val lens = mutableListOf<Int>()
        for (i in 0..8) lens.add((currentRecycler(i).adapter as SecondsListAdapter).itemCount)
        val maxLength = max(lens)
        for (i in 0..8) (currentRecycler(i).adapter as SecondsListAdapter).setLength(maxLength)
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
            if (newProject != "" && !projects.contains(newProject) &&
                !newProject.contains(';')){
                saveProject()
                projectName = newProject
                projects.add(projectName)
                txt.text = projectName
                Toast.makeText(applicationContext, "You created $projectName",
                    Toast.LENGTH_SHORT).show()
                val path = filesDir
                val file = File(path, "projects.conf")
                val content = file.readText() + "\n" + projectName
                FileOutputStream(file).write(content.toByteArray())
                clearSounds()
                dialog.dismiss()
            }
            else Toast.makeText(applicationContext, "Incorrect project name!",
                Toast.LENGTH_SHORT).show()
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

            for (i in 0..maxTracks) {
                if (sounds[i][0] != emptySound) state = "ready"
            }
        }
    }

    private fun selectSoundClicked (menuItem: MenuItem) {
        val itemTitle = menuItem.title.toString()
        currentSound = itemTitle
        txt2.text = currentSound
        txtLen.text = getSoundLength(currentSound).toString()
    }

    private fun isRawResource (name : String): Boolean {
        return resourcesArray.contains(name)
    }

    private fun clearSounds () {

        for (i in 0..8) { // очистка recycler
            (currentRecycler(i).adapter as SecondsListAdapter).eraseSounds()
        }

        for (i in 0..maxTracks) { // очистка данных по звукам
            tracks[i].release()
            tracks[i] = SoundPool(10, AudioManager.STREAM_MUSIC, 0)

            for (j in 0..maxSounds[i]) {
                sounds[i][j] = emptySound
            }

            maxSounds[i] = -1
        }

        maxTracks = 0
        state = "unready"
        setMusicLength()
    }

    private fun getMusicLength (): Long {
        return if (state != "unready") {
            val tracksLengths = mutableListOf<Long>()

            for (i in 0..maxTracks) {
                try {
                    tracksLengths.add(0)
                    for (j in 0..maxSounds[i]) tracksLengths[i] += sounds[i][j].delay
                    tracksLengths[i] += sounds[i][maxSounds[i]].len
                }
                catch (e : IndexOutOfBoundsException) {}
            }

            max(tracksLengths)
        } else 0
    }

    private fun setMusicLength(length : Long = getMusicLength(), changeRecycler : Boolean = true) {
        time.text = if (state != "unready") {
            if (changeRecycler) setMaxLength ()
            val millis = length % 1000
            val seconds = (length / 1000) % 60
            val minutes = length / 60000
            val millisString = if(millis >= 100) millis.toString()
            else {
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
        Toast.makeText(this, "Opening project $projectName...",
            Toast.LENGTH_SHORT).show()
        val path = filesDir
        val file = File(path, "$projectName.emproj")
        if (file.exists()) {
            clearSounds()
            val content: String = file.readText()
            val tracksContent = content.split("\n").toTypedArray()
            maxTracks = tracksContent.size - 1
            val missingSounds = mutableListOf<String>()
            try {
                for (i in tracksContent.indices) {
                    val soundsContent = tracksContent[i].split(";").toTypedArray()
                    maxSounds[i] = soundsContent.size - 1
                    val visualise = mutableListOf<Sound>()

                    for (j in soundsContent.indices) {
                        val params = soundsContent[j].split(" ").toTypedArray()
                        val sound = SoundInfo(
                            params[0],
                            params[1].toInt(),
                            params[2].toLong(),
                            params[3].toFloat(),
                            params[4].toInt(),
                            params[5].toFloat(),
                            (getSoundLength(params[0]) / params[5].toFloat()).toLong()
                        )
                        sounds[i][j] = sound
                        val indent = if (j != 0)
                                ((sound.delay - sounds[i][j - 1].len) / 10.0).roundToInt()
                        else (sound.delay / 10.0).roundToInt()
                        var len = (sound.len / 10.0).roundToInt()
                        len = if (len > 0) len else 1
                        visualise.add(Sound(indent, len, currentColor(i, j), i, j))
                        if (sound.len == 0.toLong() && !missingSounds.contains(params[0]))
                            missingSounds.add(params[0])
                    }

                    (currentRecycler(i).adapter as SecondsListAdapter).fillTrack(visualise)
                }
            }
            catch (e : ArrayIndexOutOfBoundsException) {
                Toast.makeText(this, "Oops! This file seems to be broken!",
                    Toast.LENGTH_SHORT).show()
            }
            state = "ready"
            setMusicLength()
            if (missingSounds.size != 0) {
                var message = "This project contains sounds not present on this device: "

                for (i in missingSounds.indices) {
                    message += missingSounds[i] + if (i != missingSounds.size - 1) ", "
                    else "."
                }

                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
        else Toast.makeText(this, "No such file!", Toast.LENGTH_SHORT).show()
    }

    private fun saveProject() {
        if (state != "unready") {
            val path = filesDir
            val file = File(path, "$projectName.emproj")
            var content = ""

            for (i in 0..maxTracks) {
                if (maxSounds[i] != -1) {
                    for (j in 0..maxSounds[i]) {
                        content += sounds[i][j].res + " "
                        content += sounds[i][j].id.toString() + " "
                        content += sounds[i][j].delay.toString() + " "
                        content += sounds[i][j].volume.toString() + " "
                        content += sounds[i][j].loop.toString() + " "
                        content += sounds[i][j].ratio.toString()
                        if (j != maxSounds[i]) content += ";"
                    }
                    if (i != maxTracks) content += "\n"
                }
            }

            FileOutputStream(file).write(content.toByteArray())
        }
    }

    fun editSelected (i : Int, j : Int) {
        infoAboutSelected(i, j)
        if (maxSounds[i] != -1) buttonDelete.setOnClickListener { deleteSelected(i, j) }
        else buttonDelete.setOnClickListener {}
        if (maxSounds[i] != -1) buttonEdit.setOnClickListener { changeSelected(i, j) }
        else buttonEdit.setOnClickListener {}
    }

    private fun infoAboutSelected (i : Int, j : Int) {
        val sound = sounds[i][j]
        currentSound = sound.res
        txt2.text = currentSound
        txtLen.text = getSoundLength(currentSound).toString()
        edittextmain1.setText(sound.delay.toString())
        edittextmain3.setText((sound.volume * 100).toInt().toString())
        edittextmain4.setText((100 / sound.ratio).toString())
    }

    private fun deleteSelected (i : Int, j: Int) {
        if (j <= maxSounds[i] && maxSounds[i] != -1) {
            val prevMusicLength = getMusicLength()
            val delay : Long = sounds[i][j].delay + sounds[i][j + 1].delay
            sounds[i][j] = sounds[i][j + 1]
            sounds[i][j].delay = delay
            for (k in (j + 1)..maxSounds[i]) sounds[i][k] = sounds[i][k + 1]
            maxSounds[i]--
            if (maxSounds[i] == -1) {
                if (sounds.contentEquals(Array(100) { Array(500) { emptySound } }))
                    state = "unready"
                if (maxTracks == i && maxTracks != 0) maxTracks --
            }
            if (prevMusicLength != getMusicLength()) setMusicLength(changeRecycler = false)
            (currentRecycler(i).adapter as SecondsListAdapter).deleteSound(j)
        }
    }

    private fun changeSelected (i : Int, j: Int) {
        if (j <= maxSounds[i]) {
            val prevMusicLength = getMusicLength()
            sounds[i][j] = getSoundParameters(i, j)
            val sound = sounds[i][j]
            val indent = if (j != 0) ((sound.delay - sounds[i][j - 1].len) / 10.0).roundToInt()
            else (sound.delay / 10.0).roundToInt()
            var len = (sound.len / 10.0).roundToInt()
            len = if (len > 0) len else 1
            (currentRecycler(i).adapter as SecondsListAdapter).editSound(
                Sound(
                indent, len, currentColor(i, j), i, j)
            )
            if (j != maxSounds[i]) {
                if (sounds[i][j + 1].delay < sound.len) {
                    sounds[i][j + 1].delay += sound.len - sounds[i][j + 1].delay
                    val nextSound = sounds[i][j + 1]
                    val nextIndent = ((nextSound.delay - sound.len) / 10.0).roundToInt()
                    var nextLen = (nextSound.len / 10.0).roundToInt()
                    nextLen = if (nextLen > 0) nextLen else 1
                    (currentRecycler(i).adapter as SecondsListAdapter).editSound(
                        Sound(
                            nextIndent, nextLen, currentColor(i, j + 1), i, j + 1)
                    )
                }
            }
            if (prevMusicLength < getMusicLength()) setMusicLength()
            else setMusicLength(changeRecycler = false)
        }
    }

    @Throws(IOException::class)
    fun readBytes(inputStream: InputStream): ByteArray? {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len: Int
        while(inputStream.read(buffer).also { len = it } != -1) byteBuffer.write(buffer, 0, len)
        return byteBuffer.toByteArray()
    }

    private fun rawResourceToFile (resourceName : String, fileName : String) {
        val res = resources
        val inStream: InputStream = res.openRawResource(res.getIdentifier(resourceName,
            "raw", packageName))
        val data = readBytes(inStream)
        val firstProject = File(filesDir, fileName)
        FileOutputStream(firstProject).write(data)
    }

    private fun getSoundLength(name: String): Long {
        return try {
            val metaRetriever = MediaMetadataRetriever()
            if (isRawResource(name)) {
                rawResourceToFile(name, "sound.wav")
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

    private fun playTrack(i: Int, j: Int, delay: Long = 0) {
        val started = played
        val handler = Handler()
        handler.postDelayed({
            val sound = sounds[i][j]
            if (state != "pause" && started == played) {
                tracks[i].play(sound.id, sound.volume, sound.volume, 0, 0, sound.ratio)
                Log.d(TAG, "MYMSG play: $i $j " + sounds[i][0].res)
            }
            if (j < maxSounds[i] && started == played) playTrack(i, j + 1)
        }, sounds[i][j].delay + delay)
    }

    private fun getSoundParameters(x : Int, y : Int): SoundInfo {
        val res : String = currentSound
        var volume : Float = abs(edittextmain3.text.toString().toFloat() / 100)
        if (volume > 1) volume = 1.0F
        if (edittextmain4.text.toString().toFloat() < 12.5) edittextmain4.setText(12.5F.toString())
        else if (edittextmain4.text.toString().toFloat() > 800)
            edittextmain4.setText(800.toString())
        val ratio : Float = abs(100 / (edittextmain4.text.toString().toFloat()))
        val len : Long = (getSoundLength(res) / ratio).toLong()
        if (y > 0) {
            val soundPrev = sounds[x][y - 1]
            if (edittextmain1.text.toString().toLong() < soundPrev.len)
                edittextmain1.setText(soundPrev.len.toString())
        }
        val delay : Long = edittextmain1.text.toString().toLong()
        return SoundInfo(res, 0, delay, volume, 0, ratio, len)
    }

    private fun setTimer (length: Long = getMusicLength()) {
        val len = getMusicLength()
        timer = object : CountDownTimer(length + 100, 1) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                if (len - timeRemaining >= 100) setMusicLength(len - timeRemaining - 100,
                    false)
            }
            override fun onFinish() {
                timeRemaining = 0
                setMusicLength(changeRecycler = false)
                state = "ready"
            }
        }.start()
    }

    fun playSound(view: View) {
        if (state == "ready") {
            played += 1
            Toast.makeText(this, "Playing compiled music...", Toast.LENGTH_SHORT).show()
            saveProject()
            setMusicLength(changeRecycler = false)

            for (i in 0..maxTracks) { // очищение и перезаполнение, если играем еще раз
                tracks[i].release()
                tracks[i] = SoundPool(10, AudioManager.STREAM_MUSIC, 0)
            }

            for (i in 0..maxTracks) {
                for (j in 0..maxSounds[i]) {
                    val res = sounds[i][j].res
                    if (isRawResource(res)) sounds[i][j].id = tracks[i].load(
                        baseContext,
                        resources.getIdentifier(res, "raw", packageName),
                        0
                    ) // загрузить i трек, j звук, если это ресурс
                    else try {
                        sounds[i][j].id = tracks[i].load("$filesDir/$res.wav",0)
                    } // загрузить i трек, j звук, если это пользовательский звук
                    catch (e: Exception) {}
                }
            }

            if (state != "playing") {
                val start: Long = currentTimeMillis() + 100
                state = "playing"
                setTimer()
                if (maxSounds[0] != -1) playTrack(0, 0, start - currentTimeMillis())
                if (maxSounds[1] != -1) playTrack(1, 0, start - currentTimeMillis())
                if (maxSounds[2] != -1) playTrack(2, 0, start - currentTimeMillis())
                if (maxSounds[3] != -1) playTrack(3, 0, start - currentTimeMillis())
                if (maxSounds[4] != -1) playTrack(4, 0, start - currentTimeMillis())
                if (maxSounds[5] != -1) playTrack(5, 0, start - currentTimeMillis())
                if (maxSounds[6] != -1) playTrack(6, 0, start - currentTimeMillis())
                if (maxSounds[7] != -1) playTrack(7, 0, start - currentTimeMillis())
                if (maxSounds[8] != -1) playTrack(8, 0, start - currentTimeMillis())
            }
        }
        else if (state == "pause") {
            Toast.makeText(this, "Music unpaused", Toast.LENGTH_SHORT).show()
            setTimer(timeRemaining)
            state = "playing"
            for (i in 0..maxTracks) tracks[i].autoResume()
        }
    }

    fun resetPlaying(view: View) {
        timer?.cancel()
        setMusicLength(changeRecycler = false)
        Toast.makeText(this, "Playing halted", Toast.LENGTH_SHORT).show()
        for (i in 0..maxTracks) { // очищение и перезаполнение, если играем еще раз
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
            for (i in 0..maxTracks) tracks[i].autoPause()
            state = "pause"
        }
    }

    fun saveProjectUI (view: View) {
        saveProject()
        if (state != "unready") Toast.makeText(this, "Saving project...",
            Toast.LENGTH_SHORT).show()
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