package com.mazharulsabbir.smsbroadcastreceiver.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.telephony.SmsMessage
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class SmsUploadService : Service() {
    private inner class ServiceHandler(message: SmsMessage?) {}

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: created()")
        val channel: NotificationChannel

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = NotificationChannel(
                CHANNEL_ID,
                "Notification from SMS Receiver",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SMS Receiver")
                .setContentText("SMS Receiver is running")
                .build()
            startForeground(NOTIFICATION_ID, notification)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand: Intent Data: ${intent?.data}")
        rawJSON()

        return super.onStartCommand(intent, flags, startId)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun rawJSON() {
        // Create JSON using JSONObject
        val jsonObject = JSONObject()
        jsonObject.put("name", "Jack")
        jsonObject.put("salary", "3540")
        jsonObject.put("age", "23")

        // Convert JSONObject to String
        val jsonObjectString = jsonObject.toString()

        GlobalScope.launch(Dispatchers.IO) {
            val url = URL("http://dummy.restapiexample.com/api/v1/create")
            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.setRequestProperty("Content-Type", "application/json") // The format of the content we're sending to the server
            httpURLConnection.setRequestProperty("Accept", "application/json") // The format of response we want to get from the server
            httpURLConnection.doInput = true
            httpURLConnection.doOutput = true

            // Send the JSON we created
            val outputStreamWriter = OutputStreamWriter(httpURLConnection.outputStream)
            outputStreamWriter.write(jsonObjectString)
            outputStreamWriter.flush()

            // Check if the connection is successful
            val responseCode = httpURLConnection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = httpURLConnection.inputStream.bufferedReader()
                    .use { it.readText() }  // defaults to UTF-8
                withContext(Dispatchers.Main) {

                    // Convert raw JSON to pretty JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(JsonParser.parseString(response))
                    Log.d(TAG, prettyJson)
                }
            } else {
                Log.e(TAG, responseCode.toString())
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.i(TAG, "onBind: called!")
        return null
    }

    override fun stopService(name: Intent?): Boolean {
        Log.i(TAG, "stopService: stopping.........")
        return super.stopService(name)
    }

    companion object {
        private const val TAG = "SmsUploadService"
        private const val CHANNEL_ID = "SmsUploadServiceChannel_001"
        private const val NOTIFICATION_ID = 127
        // https://www.tutlane.com/tutorial/android/android-progress-notification-with-examples
    }
}
