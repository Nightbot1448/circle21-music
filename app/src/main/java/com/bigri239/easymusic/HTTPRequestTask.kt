package com.bigri239.easymusic

import android.os.AsyncTask
import java.io.FileNotFoundException
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL

class HTTPRequestTask (private val url : String)  : AsyncTask<Void?, Void?, String?>(){

    override fun doInBackground(vararg params: Void?): String {
        lateinit var result: String
        try {
            val uploadUrl = URL(url)
            val connection: HttpURLConnection = uploadUrl.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            val serverResponseCode: Int = connection.responseCode
            result = if (serverResponseCode == 200) {
                try {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } finally {
                    connection.disconnect()
                }
            }
            else "0"
            connection.disconnect()
        }
        catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        catch (e: ProtocolException) {
            e.printStackTrace()
        }
        catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    fun execute () : String {
        return doInBackground()
    }
}