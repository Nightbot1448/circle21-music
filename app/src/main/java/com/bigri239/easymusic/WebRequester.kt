package com.bigri239.easymusic

import android.content.Context
import android.os.AsyncTask
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.Serializable
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class WebRequester (private val context: Context) {

    private val baseURL = "https://bialger.com/easymusic/"
    var hashed = checkHashed()
    var hashedLogin = hashed[0]
    var hashedPassword = hashed[1]
    var lid = hashed[2]
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
        var id = ""
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
        var paramsGET = ""
        if (params.isNotEmpty()) {
            paramsGET = "?"
            for ((param, value) in params.entries) paramsGET += "$param=$value&"
            paramsGET = paramsGET.dropLast(1)
        }
        val connection = URL(baseURL + subAddress + paramsGET).openConnection() as HttpURLConnection
        var answer = ""
        try {
            answer = connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
        return answer.split("\n").toTypedArray()
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
}
