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
import android.text.Editable
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
import java.lang.Math.round
import java.lang.System.currentTimeMillis
import kotlin.math.roundToInt


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
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
    private var mAdapter: RecyclerAdapter? = null
    private var mRecyclerView: RecyclerView? = null
    private lateinit var recyclerViewhor1: RecyclerView
    private lateinit var recyclerViewhor2: RecyclerView
    private lateinit var recyclerViewhor3: RecyclerView
    private lateinit var recyclerViewhor4: RecyclerView
    private lateinit var recyclerViewhor5: RecyclerView
    private lateinit var recyclerViewhor6: RecyclerView
    private lateinit var recyclerViewhor7: RecyclerView
    private lateinit var recyclerViewhor8: RecyclerView
    private lateinit var recyclerViewhor9: RecyclerView
    private lateinit var btnAdd1: Button
    private lateinit var btnAdd2: Button
    private lateinit var btnAdd3: Button
    private lateinit var btnAdd4: Button
    private lateinit var btnAdd5: Button
    private lateinit var btnAdd6: Button
    private lateinit var btnAdd7: Button
    private lateinit var btnAdd8: Button
    private lateinit var btnAdd9: Button
    /*private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                saveMusic()
            } else {
                Toast.makeText(this, "Oops! You did not give permission to write files! :(", Toast.LENGTH_LONG).show()
            }
        }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        recyclerViewhor1 = findViewById(R.id.recyclerViewhor1)
        recyclerViewhor1.adapter = SecondsListAdapter()
        (recyclerViewhor1.adapter as SecondsListAdapter).notifyDataSetChanged()
        recyclerViewhor2 = findViewById(R.id.recyclerViewhor2)
        recyclerViewhor2.adapter = SecondsListAdapter()
        (recyclerViewhor1.adapter as SecondsListAdapter).notifyDataSetChanged()
        recyclerViewhor3 = findViewById(R.id.recyclerViewhor3)
        recyclerViewhor3.adapter = SecondsListAdapter()
        (recyclerViewhor1.adapter as SecondsListAdapter).notifyDataSetChanged()
        recyclerViewhor4 = findViewById(R.id.recyclerViewhor4)
        recyclerViewhor4.adapter = SecondsListAdapter()
        (recyclerViewhor1.adapter as SecondsListAdapter).notifyDataSetChanged()
        recyclerViewhor5 = findViewById(R.id.recyclerViewhor5)
        recyclerViewhor5.adapter = SecondsListAdapter()
        (recyclerViewhor1.adapter as SecondsListAdapter).notifyDataSetChanged()
        recyclerViewhor6 = findViewById(R.id.recyclerViewhor6)
        recyclerViewhor6.adapter = SecondsListAdapter()
        (recyclerViewhor1.adapter as SecondsListAdapter).notifyDataSetChanged()
        recyclerViewhor7 = findViewById(R.id.recyclerViewhor7)
        recyclerViewhor7.adapter = SecondsListAdapter()
        (recyclerViewhor1.adapter as SecondsListAdapter).notifyDataSetChanged()
        recyclerViewhor8 = findViewById(R.id.recyclerViewhor8)
        recyclerViewhor8.adapter = SecondsListAdapter()
        (recyclerViewhor1.adapter as SecondsListAdapter).notifyDataSetChanged()
        recyclerViewhor9 = findViewById(R.id.recyclerViewhor9)
        recyclerViewhor9.adapter = SecondsListAdapter()
        (recyclerViewhor1.adapter as SecondsListAdapter).notifyDataSetChanged()

        btnAdd1 = findViewById(R.id.btnAdd1)
        btnAdd2 = findViewById(R.id.btnAdd2)
        btnAdd3 = findViewById(R.id.btnAdd3)
        btnAdd4 = findViewById(R.id.btnAdd4)
        btnAdd5 = findViewById(R.id.btnAdd5)
        btnAdd6 = findViewById(R.id.btnAdd6)
        btnAdd7 = findViewById(R.id.btnAdd7)
        btnAdd8 = findViewById(R.id.btnAdd8)
        btnAdd9 = findViewById(R.id.btnAdd9)
        btnAdd1.setOnClickListener {
            val x = 0
            if (state == "unready") state = "ready"
            if (countTracks < x) countTracks = x
            val sound = getSoundParameters()
            countSounds[x]++
            sounds[x][countSounds[x]] = sound
            (recyclerViewhor1.adapter as SecondsListAdapter).addSound(Sound(
                (edittextmain1.text.toString().toFloat() / 100).roundToInt(),
                ((getSoundLength(sound.res) / sound.ratio) / 100).roundToInt(),
                SoundType. SOUND1))
        }
        btnAdd2.setOnClickListener {
            val x = 1
            if (state == "unready") state = "ready"
            if (countTracks < x) countTracks = x
            val sound = getSoundParameters()
            countSounds[x]++
            sounds[x][countSounds[x]] = sound
            (recyclerViewhor2.adapter as SecondsListAdapter).addSound(Sound(
                (edittextmain1.text.toString().toFloat() / 100).roundToInt(),
                ((getSoundLength(sound.res) / sound.ratio) / 100).roundToInt(),
                SoundType. SOUND2))
        }
        btnAdd3.setOnClickListener {
            val x = 2
            if (state == "unready") state = "ready"
            if (countTracks < x) countTracks = x
            val sound = getSoundParameters()
            countSounds[x]++
            sounds[x][countSounds[x]] = sound
            (recyclerViewhor3.adapter as SecondsListAdapter).addSound(Sound(
                (edittextmain1.text.toString().toFloat() / 100).roundToInt(),
                ((getSoundLength(sound.res) / sound.ratio) / 100).roundToInt(),
                SoundType. SOUND3))
        }
        btnAdd4.setOnClickListener {
            val x = 3
            if (state == "unready") state = "ready"
            if (countTracks < x) countTracks = x
            val sound = getSoundParameters()
            countSounds[x]++
            sounds[x][countSounds[x]] = sound
            (recyclerViewhor4.adapter as SecondsListAdapter).addSound(Sound(
                (edittextmain1.text.toString().toFloat() / 100).roundToInt(),
                ((getSoundLength(sound.res) / sound.ratio) / 100).roundToInt(),
                SoundType. SOUND4))
        }
        btnAdd5.setOnClickListener {
            val x = 4
            if (state == "unready") state = "ready"
            if (countTracks < x) countTracks = x
            val sound = getSoundParameters()
            countSounds[x]++
            sounds[x][countSounds[x]] = sound
            (recyclerViewhor5.adapter as SecondsListAdapter).addSound(Sound(
                (edittextmain1.text.toString().toFloat() / 100).roundToInt(),
                ((getSoundLength(sound.res) / sound.ratio) / 100).roundToInt(),
                SoundType. SOUND5))
        }
        btnAdd6.setOnClickListener {
            val x = 5
            if (state == "unready") state = "ready"
            if (countTracks < x) countTracks = x
            val sound = getSoundParameters()
            countSounds[x]++
            sounds[x][countSounds[x]] = sound
            (recyclerViewhor6.adapter as SecondsListAdapter).addSound(Sound(
                (edittextmain1.text.toString().toFloat() / 100).roundToInt(),
                ((getSoundLength(sound.res) / sound.ratio) / 100).roundToInt(),
                SoundType.SOUND1))
        }
        btnAdd7.setOnClickListener {
            val x = 6
            if (state == "unready") state = "ready"
            if (countTracks < x) countTracks = x
            val sound = getSoundParameters()
            countSounds[x]++
            sounds[x][countSounds[x]] = sound
            (recyclerViewhor7.adapter as SecondsListAdapter).addSound(Sound(
                (edittextmain1.text.toString().toFloat() / 100).roundToInt(),
                ((getSoundLength(sound.res) / sound.ratio) / 100).roundToInt(),
                SoundType. SOUND2))
        }
        btnAdd8.setOnClickListener {
            val x = 7
            if (state == "unready") state = "ready"
            if (countTracks < x) countTracks = x
            val sound = getSoundParameters()
            countSounds[x]++
            sounds[x][countSounds[x]] = sound
            (recyclerViewhor8.adapter as SecondsListAdapter).addSound(Sound(
                (edittextmain1.text.toString().toFloat() / 100).roundToInt(),
                ((getSoundLength(sound.res) / sound.ratio) / 100).roundToInt(),
                SoundType. SOUND3))
        }
        btnAdd9.setOnClickListener {
            val x = 8
            if (state == "unready") state = "ready"
            if (countTracks < x) countTracks = x
            val sound = getSoundParameters()
            countSounds[x]++
            sounds[x][countSounds[x]] = sound
            (recyclerViewhor9.adapter as SecondsListAdapter).addSound(Sound(
                (edittextmain1.text.toString().toFloat() / 100).roundToInt(),
                ((getSoundLength(sound.res) / sound.ratio) / 100).roundToInt(),
                SoundType. SOUND4))
        }

        val textView = findViewById<TextView>(R.id.txt)
        textView.setOnClickListener(viewClickListener)
        // create_horizontal_list()
//        create_sounds_list()
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
        val intent14 = Intent(this, RecoveryActivity::class.java)
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
        else { // TODO: после написания рабочего recycler, надо вставить его обновление в конец и в диалог
            saveProject()
            projectName = itemTitle
            txt.text = projectName
            openProject()
            state = "unready"
            for (i in 0..countTracks) { // очистка данных по звукам
                if (sounds[i][0] != SoundInfo("", i + 1, 0, 1.0F, 0, 1.0F)) {
                    state = "ready"
                }
            }
        }
    }

    /*private fun create_sounds_list() {
        val sound_list = mutableListOf(
            "Kick", "Type1", "Snare",
            "Type1", "Hihat", "Type1",
            "Loop", "Type1", "Bass",
            "Type1", "808", "Type1",
            "+", "Type1", "+",
            "Type1"
        )
        val on_sound_click =  { position: Int, text_item: TextView ->
            sound_list[position] = "changed"
            text_item.text = sound_list[position]
            Log.d("MYMSG vert_recyc: ", sound_list[position])
            Unit
        }
        GridLayoutManager(
            this, // context
            2, // span count
            RecyclerView.VERTICAL, // orientation
            false // reverse layout
        ).apply {
            // specify the layout manager for recycler view
            recyclerView.layoutManager = this
        }
        recyclerView.adapter = RecyclerViewAdapter(sound_list, on_sound_click)
    }*/

    private fun create_select_project_popup_menu(v: View) {
        val popupMenu = PopupMenu(this, v)
        for (i in projects.indices) popupMenu.menu.add(projects[i])
        popupMenu.inflate(R.menu.popupmenu)
        popupMenu.setOnMenuItemClickListener { project_select_popup_menu_click_listener(it); true }
        popupMenu.show()
    }

    /*private fun create_horizontal_list() {
        mRecyclerView = findViewById(R.id.recyclerViewhor)
        mRecyclerView?.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL, false
        )
        val dataset = arrayOfNulls<String>(50)
        for (i in dataset.indices) {
            dataset[i] = "item$i"
        }
        mAdapter = RecyclerAdapter(dataset, this)
        mRecyclerView?.adapter = mAdapter
    }*/

    private fun isRawResource (name : String): Boolean {
        val resourcesArray : Array<String> = arrayOf("file1", "file2", "memories1", "yf___vinnyx__crash_")
        return resourcesArray.contains(name)
    }

    private fun bytesArrayPart4ToInt(arr: ByteArray, start: Int = 0): Int {
        return arr[start].toInt() + arr[start + 1].toInt() * 256 + arr[start + 2].toInt() * 256 * 256 + arr[start + 3].toInt() * 256 * 256 * 256
    }

    private fun openProject() {
        try {
            Toast.makeText(this, "Opening project $projectName...", Toast.LENGTH_SHORT).show()
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

    /*private fun commonMusicFile(fileName: String): File {
        val dir: File = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), fileName)
        }
        else File(Environment.getExternalStorageDirectory(), fileName)
        return dir
    }*/

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
                    tracks[i].play(sound.id, sound.volume, sound.volume, 0, sound.loop, sound.ratio)
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

    private fun getSoundParameters(): SoundInfo {
        val res : String = edittextmain2.text.toString()
        val volume : Float = edittextmain3.text.toString().toFloat() / 100
        val ratio : Float = 100 / (edittextmain4.text.toString().toFloat())
        Log.d(TAG, "MYMSG param: $res")
        val delay : Long = (edittextmain1.text.toString().toFloat() + getSoundLength(res) / ratio).toLong()
        return SoundInfo(res, 0, delay, volume, 0, ratio)
    }

    /*private fun saveMusic() {
        val file = commonMusicFile("$projectName.wav")
        val recorder = MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(file.absolutePath);
        recorder.prepare();
        recorder.start();

        recorder.stop();
        recorder.reset();
        recorder.release();
    }*/

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
        } else if (state == "pause") {
            Toast.makeText(this, "Music unpaused", Toast.LENGTH_SHORT).show()
            state = "playing"
            for (i in 0..countTracks) tracks[i].autoResume()
        }
    }

    fun saveProjectUI (view: View) {
        saveProject()
        if (state != "unready") Toast.makeText(this, "Saving project...", Toast.LENGTH_SHORT).show()
    }

    /*@RequiresApi(Build.VERSION_CODES.R)
    fun saveMusicUI(view: View) {
        if (state != "unready") {
            Toast.makeText(this, "Saving music...", Toast.LENGTH_SHORT).show()
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    baseContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) -> saveMusic()
                else -> requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }*/

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