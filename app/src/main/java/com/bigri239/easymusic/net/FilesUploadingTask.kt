package com.bigri239.easymusic.net

import android.os.AsyncTask
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL
import kotlin.math.min

@Suppress("DEPRECATION")
class FilesUploadingTask (private val filePath: String, private val url : String) : AsyncTask<Void?, Void?, String?>() {
    // Конец строки
    private val lineEnd = "\r\n"

    // Два тире
    private val twoHyphens = "--"

    // Разделитель
    private val boundary = "----WebKitFormBoundary9xFB2hiUhzqbBQ4M"

    // Переменные для считывания файла в оперативную память
    private var bytesRead = 0
    private var bytesAvailable = 0
    private var bufferSize = 0
    private lateinit var buffer: ByteArray
    private val maxBufferSize = 1 * 1024 * 1024


    companion object {

        // Ключ, под которым файл передается на сервер
        const val FORM_FILE_NAME = "userfile"

        // Считка потока в строку
        @Throws(IOException::class)
        fun readStream(inputStream: InputStream?): String {
            val buffer = StringBuffer()
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                buffer.append(line)
            }
            return buffer.toString()
        }
    }

    override fun doInBackground(vararg params: Void?): String {
        // Результат выполнения запроса, полученный от сервера
        lateinit var result: String
        try {
            // Создание ссылки для отправки файла
            val uploadUrl = URL(url)

            // Создание соединения для отправки файла
            val connection: HttpURLConnection = uploadUrl.openConnection() as HttpURLConnection

            // Разрешение ввода соединению
            connection.doInput = true
            // Разрешение вывода соединению
            connection.doOutput = true
            // Отключение кеширования
            connection.useCaches = false

            // Задание запросу типа POST
            connection.requestMethod = "POST"

            // Задание необходимых свойств запросу
            connection.setRequestProperty("Connection", "Keep-Alive")
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=$boundary")

            // Создание потока для записи в соединение
            val outputStream = DataOutputStream(connection.outputStream)

            // Формирование multipart контента

            // Начало контента
            outputStream.writeBytes(twoHyphens + boundary + lineEnd)
            // Заголовок элемента формы
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                    FORM_FILE_NAME + "\"; filename=\"" + filePath + "\"" + lineEnd)
            // Тип данных элемента формы
            outputStream.writeBytes("Content-Type: image/jpeg$lineEnd")
            // Конец заголовка
            outputStream.writeBytes(lineEnd)

            // Поток для считывания файла в оперативную память
            val fileInputStream = FileInputStream(File(filePath))
            bytesAvailable = fileInputStream.available()
            bufferSize = min(bytesAvailable, maxBufferSize)
            buffer = ByteArray(bufferSize)

            // Считывание файла в оперативную память и запись его в соединение
            bytesRead = fileInputStream.read(buffer, 0, bufferSize)
            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize)
                bytesAvailable = fileInputStream.available()
                bufferSize = min(bytesAvailable, maxBufferSize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize)
            }

            // Конец элемента формы
            outputStream.writeBytes(lineEnd)
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)

            // Получение ответа от сервера
            val serverResponseCode: Int = connection.responseCode

            // Закрытие соединений и потоков
            fileInputStream.close()
            outputStream.flush()
            outputStream.close()

            // Считка ответа от сервера в зависимости от успеха
            result = if (serverResponseCode == 200) {
                readStream(connection.inputStream)
            } else {
                readStream(connection.errorStream)
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: ProtocolException) {
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    fun execute () : String {
        return doInBackground()
    }
}