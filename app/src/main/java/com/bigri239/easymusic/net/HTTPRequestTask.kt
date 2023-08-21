package com.bigri239.easymusic.net

import android.os.AsyncTask
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL

@Suppress("DEPRECATION")
class HTTPRequestTask (private val url : String)  : AsyncTask<Void?, Void?, String?>(){

    @Deprecated("Deprecated in Java")
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
            result = "0"
        }
        catch (e: ProtocolException) {
            result = "0"
        }
        return result
    }

    fun execute () : String {
        return doInBackground()
    }
}