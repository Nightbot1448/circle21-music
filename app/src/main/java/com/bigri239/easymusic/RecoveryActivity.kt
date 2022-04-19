package com.bigri239.easymusic

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigri239.easymusic.adapter.*
import com.bigri239.easymusic.net.WebRequester
import kotlinx.android.synthetic.main.activity_recovery.*
import java.io.*

@Suppress("DEPRECATION")
class RecoveryActivity : AppCompatActivity() {

    private var friendsList = mutableListOf<String>()
    private var soundsList = mutableListOf<String>()
    private var projectsList = mutableListOf<String>()
    private lateinit var webRequester : WebRequester
    private lateinit var friendsAdapter: CustomAdapter
    private lateinit var soundsAdapter: CustomAdapter
    private lateinit var projectsAdapter: CustomAdapter
    private val projects = mutableListOf<String>()
    private val customArray = mutableListOf<String>()
    private lateinit var email : String

    private val connectorProject = object : CustomConnector {
        override fun function(string: String) {
            loadProject(string)
        }
    }

    private val connectorSound = object : CustomConnector {
        override fun function(string: String) {}
    }

    private val connectorFriend = object : CustomConnector {
        override fun function(string: String) {
            seeFriend(string)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recovery)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        webRequester = WebRequester(this@RecoveryActivity)
    }

    override fun onStart() {
        super.onStart()
        val info = webRequester.getInfo()
        if (!info.contentEquals(Array (5) {arrayOf("")})) {
            email = info[0][0]
            username.text = "Username: " + info[0][0]
            editInfo.setText(info[1][0])
            friendsList = info[2] as MutableList
            soundsList = info[3] as MutableList
            projectsList = info[4] as MutableList
            if (friendsList == mutableListOf("")) friendsList = mutableListOf()
            if (soundsList == mutableListOf("")) soundsList = mutableListOf()
            if (projectsList == mutableListOf("")) projectsList = mutableListOf()
        }
        else {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }

        val path = filesDir
        var file = File(path, "projects.conf")

        if (file.exists()) {
            val content: String = file.readText()
            projects.clear()
            projects.addAll(content.split("\n").toTypedArray())
        }
        else {
            projects.clear()
            projects.add("projectDefault")
            var content = ""
            for (i in projects.indices) {
                content += projects[i]
                if (i != projects.size - 1) content += "\n"
            }
            FileOutputStream(file).write(content.toByteArray())
            projectToDefault()

        }

        file = File(path, "sounds.conf")

        if (file.exists()) {
            customArray.clear()
            val content: String = file.readText()
            if (content != "") customArray.addAll(content.split("\n").toTypedArray())
        }
        else {
            val content = ""
            FileOutputStream(file).write(content.toByteArray())
        }

        val intent = Intent(this, MainActivity::class.java)
        backrec.setOnClickListener {
            backrec.isClickable = false
            startActivity(intent)
        }

        friendsAdapter = CustomAdapter(friendsList, connectorFriend)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = friendsAdapter

        soundsAdapter = CustomAdapter(soundsList, connectorSound)
        recyclerView2.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView2.adapter = soundsAdapter

        projectsAdapter = CustomAdapter(projectsList, connectorProject)
        recyclerView3.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView3.adapter = projectsAdapter
    }

    private fun showFriendDialog() {
        val dialog = Dialog(this, R.style.ThemeOverlay_Material3_Dialog)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_new_project)
        dialog.findViewById<EditText>(R.id.newname).setHint("Friend`s email")
        dialog.findViewById<Button>(R.id.create).text = "Add friend"
        dialog.findViewById<Button>(R.id.create).setOnClickListener {
            val newFriend = dialog.findViewById<EditText>(R.id.newname).text.toString()
            if (newFriend != "" && !friendsList.contains(newFriend) && newFriend.contains('@')
                && !newFriend.contains(';')) {
                if (webRequester.changeInfo("friends", newFriend)) {
                    friendsList.add(newFriend)
                    (recyclerView.adapter as CustomAdapter).notifyItemInserted(
                        friendsList.size - 1)
                    dialog.dismiss()
                    Toast.makeText(this, "Friend added successfully!",
                        Toast.LENGTH_SHORT).show()
                }
                else {
                    dialog.dismiss()
                    Toast.makeText(this, "No such user!", Toast.LENGTH_SHORT).show()
                }
            }
            else Toast.makeText(this, "Incorrect username!", Toast.LENGTH_SHORT).show()
        }
        dialog.show()
    }

    private fun showSoundDialog(soundName : String) {
        val dialog = Dialog(this, R.style.ThemeOverlay_Material3_Dialog)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_new_project)
        dialog.findViewById<EditText>(R.id.newname).setHint("Link to sound")
        dialog.findViewById<Button>(R.id.create).text = "Add sound"
        dialog.findViewById<Button>(R.id.create).setOnClickListener {
            var newSound = dialog.findViewById<EditText>(R.id.newname).text.toString()
            if (newSound != "" && !soundsList.contains(newSound) &&
                newSound.contains("http") && !"$soundName $newSound".contains(';')) {
                newSound = newSound.replace("&", "AMPERSAND")
                if (webRequester.changeInfo("sounds", "$soundName $newSound")) {
                    soundsList.add("$soundName $newSound")
                    (recyclerView2.adapter as CustomAdapter).notifyItemInserted(
                        soundsList.size - 1)
                    dialog.dismiss()
                    Toast.makeText(this, "Sound added successfully!",
                        Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this, "Incorrect input!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun projectSelectPopupMenuClickListener(menuItem: MenuItem) {
        val itemTitle = menuItem.title.toString()
        if (!itemTitle.contains(';')) {
            if (webRequester.uploadProject(itemTitle)) {
                projectsList.add(itemTitle)
                (recyclerView3.adapter as CustomAdapter).notifyItemInserted(
                    projectsList.size - 1)
                Toast.makeText(this, "Project $itemTitle uploaded successfully!",
                    Toast.LENGTH_SHORT).show()
            }
            else Toast.makeText(this, "Oops! Something went wrong!",
                Toast.LENGTH_SHORT).show()
        }
        else Toast.makeText(this, "Incorrect file name!", Toast.LENGTH_SHORT).show()
    }

    private fun soundSelectPopupMenuClickListener(menuItem: MenuItem) {
        val itemTitle = menuItem.title.toString()
        showSoundDialog(itemTitle)
    }

    private fun createSelectProjectPopupMenu(v : View) {
        val popupMenu = PopupMenu(this, v)
        for (i in projects.indices) popupMenu.menu.add(projects[i])
        popupMenu.setOnMenuItemClickListener { projectSelectPopupMenuClickListener(it); true }
        popupMenu.show()
    }

    private fun loadProject(string: String) {
        if (webRequester.getProject(email, string)) Toast.makeText(this,
            "Project $string downloaded", Toast.LENGTH_SHORT).show()
        else Toast.makeText(this,
            "Oops! Something went wrong", Toast.LENGTH_SHORT).show()
    }

    private fun seeFriend(string: String) {
        val intent = Intent(this, FriendActivity::class.java)
        intent.putExtra("owner", string)
        startActivity(intent)
    }

    @Throws(IOException::class)
    private fun readBytes(inputStream: InputStream): ByteArray? {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len: Int
        while(inputStream.read(buffer).also { len = it } != -1) byteBuffer.write(buffer, 0, len)
        return byteBuffer.toByteArray()
    }

    private fun projectToDefault () {
        val res = resources
        val inStream: InputStream = res.openRawResource(res.getIdentifier("project",
            "raw", packageName))
        val data = readBytes(inStream)
        val firstProject = File(filesDir, "projectDefault.emproj")
        FileOutputStream(firstProject).write(data)
    }

    fun logOff (view: View) {
        if (webRequester.logOff()) {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }
    }

    fun changeAbout (view: View) {
        val newInfo = editInfo.text.toString()
        if (webRequester.changeInfo("about", newInfo)) Toast.makeText(this,
            "Edited successfully", Toast.LENGTH_SHORT).show()
        else Toast.makeText(this, "Oops! Something went wrong!",
            Toast.LENGTH_SHORT).show()
    }

    fun addFriend (view: View) {
        showFriendDialog()
    }

    fun addSound (view: View) {
        val popupMenu = PopupMenu(this, view)
        for (i in customArray.indices) popupMenu.menu.add(customArray[i])
        popupMenu.setOnMenuItemClickListener { soundSelectPopupMenuClickListener(it); true }
        popupMenu.show()
    }

    fun uploadProject (view: View) {
        createSelectProjectPopupMenu(view)
    }
}