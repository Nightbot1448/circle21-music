package com.bigri239.easymusic

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class WebRequester (private val context: Context) {

    private val baseURL = "https://bialger.com/easymusic/"
    private var hashed = checkHashed()
    private var hashedLogin = hashed[0]
    private var hashedPassword = hashed[1]
    private var lid = hashed[2]
    private var uuid = getUUID()

    private fun checkHashed(): Array<String> {
        var res = arrayOf("", "", "")
        try {
            val file = File(context.filesDir, "login.conf")
            res = file.readText().split("\n").toTypedArray()
        }
        catch (e : IOException) {}
        return res
    }

    private fun getUUID(): String {
        var id: String
        try {
            val file = File(context.filesDir, "uuid.conf")
            id = file.readText()
        }
        catch (e : IOException) {
            id = UUID.randomUUID().toString()
            val file = File(context.filesDir, "uuid.conf")
            FileOutputStream(file).use {
                it.write(id.toByteArray())
            }
        }
        return id
    }

    private fun baseRequest (subAddress : String, params : Map<String, String>): Array<String> {
        //try {
            var paramsGET = ""
            if (params.isNotEmpty()) {
                paramsGET = "?"
                for ((param, value) in params.entries) paramsGET += "$param=$value&"
                paramsGET = paramsGET.dropLast(1)
            }
            val requester = HTTPRequestTask(baseURL + subAddress + paramsGET)
            val answer = requester.execute()
            return answer.split("\n").toTypedArray()
        /*}
        catch (e: Exception) {
            return arrayOf("")
        }*/
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
            FileOutputStream(file).use {
                it.write("$hashedLogin\n$hashedPassword\n$lid".toByteArray())
            }
            true
        }
        else return false
    }

    fun checkAuthorized () : Boolean{
        return if (!hashed.contentEquals(arrayOf("", "", ""))) {
            val params = mapOf("user" to hashedLogin, "passw" to hashedPassword, "lid" to lid, "mac" to uuid)
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
            val params = mapOf("user" to hashedLogin, "passw" to hashedPassword, "lid" to lid, "mac" to uuid)
            val answer = baseRequest("get_info.php", params)
            if (answer[0] == "1") {
                val friends = answer[3].split(" ").toList()
                val sounds = answer[4].split(" ").toList()
                val projects = answer[5].split(" ").toList()
                arrayOf(arrayListOf(answer[1]), arrayListOf(answer[2]), friends, sounds, projects)
            }
            else Array (5) {arrayListOf("")}
        }
        else Array (5) {arrayListOf("")}
    }

    fun changeInfo (edit : String, value : String) : Boolean {
        val params = mapOf("edit" to edit, "value" to value, "user" to hashedLogin, "passw" to hashedPassword, "lid" to lid, "mac" to uuid)
        return baseRequest("change_info.php", params)[0] == "1"
    }
    
    fun uploadProject (projectName : String) : Boolean {
        var response = false
        try {
            val fileName = context.filesDir.toString() + "/" + "$projectName.emproj"
            val params = mapOf("user" to hashedLogin, "passw" to hashedPassword, "lid" to lid, "mac" to uuid)
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
}
