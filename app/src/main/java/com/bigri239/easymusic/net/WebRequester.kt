package com.bigri239.easymusic.net

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class WebRequester (private val context: Context) {

    private val baseURL = "https://bialger.com/easymusic/"
    private var hashed = checkHashed()
    private var hashedLogin = hashed[0]
    private var hashedPassword = hashed[1]
    private var lid = hashed[2]
    private var uuid = getUUID()

    private fun checkHashed(): Array<String> {
        val file = File(context.filesDir, "login.conf")
        return if (file.exists()) {
            file.readText().split("\n").toTypedArray()
        }
        else arrayOf("", "", "")
    }

    private fun getUUID(): String {
        val file = File(context.filesDir, "uuid.conf")
        return if (file.exists()) file.readText()
        else {
            val id = UUID.randomUUID().toString()
            FileOutputStream(file).write(id.toByteArray())
             id
        }
    }

    private fun baseRequest (subAddress : String, params : Map<String, String>): Array<String> {
        return try {
            var paramsGET = ""
            if (params.isNotEmpty()) {
                paramsGET = "?"
                for ((param, value) in params.entries) paramsGET += "$param=$value&"
                paramsGET = paramsGET.dropLast(1)
            }
            val requester = HTTPRequestTask(baseURL + subAddress + paramsGET)
            val answer = requester.execute()
            answer.split("\n").toTypedArray()
        } catch (e: Exception) {
            arrayOf("")
        }
    }

    fun logOff () : Boolean {
        val file = File(context.filesDir, "login.conf")
        return if (file.exists()) {
            file.delete()
            true
        }
        else false
    }

    fun logIn (login : String, password : String): Boolean {
        val params = mapOf("login" to login, "password" to password, "mac" to uuid)
        val response = baseRequest("auth.php", params)
        return if (response[0] == "1") {
            hashedLogin = response[1]
            hashedPassword = response[2]
            lid = response[3]
            val file = File(context.filesDir, "login.conf")
            FileOutputStream(file).write("$hashedLogin\n$hashedPassword\n$lid".toByteArray())
            true
        }
        else return false
    }

    fun checkAuthorized () : Boolean{
        return if (!hashed.contentEquals(arrayOf("", "", ""))) {
            val params = mapOf("user" to hashedLogin, "passw" to hashedPassword, "lid" to lid,
                "mac" to uuid)
            baseRequest("prove_login.php", params)[0] == "1"
        }
        else false
    }

    fun signUp (login : String, password : String): Boolean {
        val params = mapOf("login" to login, "password" to password)
        return baseRequest("user_req.php", params)[0] == "1"
    }

    fun getInfo () : Array<List<String>> {
        return if (!hashed.contentEquals(arrayOf("", "", ""))) {
            val params = mapOf("user" to hashedLogin, "passw" to hashedPassword, "lid" to lid,
                "mac" to uuid)
            val answer = baseRequest("get_info.php", params)
            if (answer[0] == "1") {
                val friends = answer[3].split(";").toList()
                val sounds = answer[4].split(";").toList()
                val projects = answer[5].split(";").toList()
                arrayOf(arrayListOf(answer[1]), arrayListOf(answer[2]), friends, sounds, projects)
            }
            else Array (5) {arrayListOf("")}
        }
        else Array (5) {arrayListOf("")}
    }

    fun changeInfo (edit : String, value : String) : Boolean {
        val params = mapOf("edit" to edit, "value" to value, "user" to hashedLogin,
            "passw" to hashedPassword, "lid" to lid, "mac" to uuid)
        return baseRequest("change_info.php", params)[0] == "1"
    }
    
    fun uploadProject (projectName : String) : Boolean {
        var response = false
        try {
            val fileName = context.filesDir.toString() + "/" + "$projectName.emproj"
            val params = mapOf("user" to hashedLogin, "passw" to hashedPassword, "lid" to lid,
                "mac" to uuid)
            var paramsGET = "?"
            for ((param, value) in params.entries) paramsGET += "$param=$value&"
            paramsGET = paramsGET.dropLast(1)
            val url = baseURL + "upload_project.php" + paramsGET
            try {
                val uploader = FilesUploadingTask(fileName, url)
                val answer = uploader.execute()
                response = answer.dropLast(answer.length - 1) == "1"
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
        catch (e : IOException) {
            response = false
        }
        return response
    }

    fun getProject (owner : String, projectName: String) : Boolean{
        val params = mapOf("owner" to owner, "name" to projectName, "user" to hashedLogin,
            "passw" to hashedPassword, "lid" to lid, "mac" to uuid)
        val answer = baseRequest("get_project.php", params)
        return if (answer[0] == "1") {
            val content = answer.slice(1 until answer.size).joinToString("\n")
            var file = File(context.filesDir, "$projectName.emproj")
            FileOutputStream(file).write(content.toByteArray())
            file = File(context.filesDir, "projects.conf")
            if (file.exists()) {
                if (file.readText() != "") {
                    val projects = mutableListOf<String>()
                    projects.addAll(file.readText().split("\n").toTypedArray())
                    if (!projects.contains(projectName)) file.appendText("\n$projectName")
                }
                else file.appendText("projectDefault\n$projectName")
            }
            else {
                FileOutputStream(file).write("projectDefault\n$projectName".toByteArray())
            }
            true
        }
        else false
    }

    fun getFriendInfo (owner: String) : Array<List<String>> {
        return if (!hashed.contentEquals(arrayOf("", "", ""))) {
            val params = mapOf("owner" to owner, "user" to hashedLogin, "passw" to hashedPassword,
                "lid" to lid, "mac" to uuid)
            val answer = baseRequest("get_friend_info.php", params)
            if (answer[0] == "1") {
                val friends = answer[3].split(";").toList()
                val sounds = answer[4].split(";").toList()
                val projects = answer[5].split(";").toList()
                arrayOf(arrayListOf(answer[1]), arrayListOf(answer[2]), friends, sounds, projects)
            }
            else Array (5) {arrayListOf("")}
        }
        else Array (5) {arrayListOf("")}
    }
}
